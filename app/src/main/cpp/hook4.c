#include <stdio.h>
#include <stdlib.h>
#include <sys/user.h>
#include <asm/ptrace.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <dlfcn.h>
#include <dirent.h>
#include <unistd.h>
#include <string.h>
#include <elf.h>
#include <android/log.h>
#include <arm-linux-androideabi/asm/ptrace.h>
#include <errno.h>
//T标志位：该位反映处理器的运行状态。当该位为1时，程序运行于THUMB状态，否则运行于ARM状态。//
#define CPSR_T_MASK     ( 1u << 5 )//32位CPSR寄存器
const char *libc_path = "/system/lib/libc.so";

const int long_size = sizeof(long);

int ptrace_setregs(pid_t pid, struct pt_regs * regs)
{
    if (ptrace(PTRACE_SETREGS, pid, NULL, regs) < 0) {
        perror("ptrace_setregs: Can not set register values");
        return -1;
    }

    return 0;
}

int ptrace_continue(pid_t pid)
{
    if (ptrace(PTRACE_CONT, pid, NULL, 0) < 0) {
        perror("ptrace_cont");
        return -1;
    }

    return 0;
}

void putdata(pid_t child, long addr,
             char *str, int len)
{   //child 目标进程
	//addr 堆栈指针2栈顶
	//str 参数指针
	//len 参数字节数
	char *laddr;
    int i, j;
    union u {
            long val;
            char chars[long_size];
    }data;
    i = 0;
    j = len / long_size;
    laddr = str;
    while(i < j) {
        memcpy(data.chars, laddr, long_size);//从laddr拷贝long_size个字节到data.chars
        ptrace(PTRACE_POKEDATA, child,
               addr + i * 4, data.val);
        ++i;//sp向高地址移动4位
        laddr += long_size;//取下一个参数
    }
    j = len % long_size;
    if(j != 0) {//怎么会有这种情况？
        memcpy(data.chars, laddr, j);
        ptrace(PTRACE_POKEDATA, child,
               addr + i * 4, data.val);
    }
}

/*获取模块基址*/
void* get_module_base(pid_t pid, const char* module_name)
{
    FILE *fp;
    long addr = 0;
    char *pch;
    char filename[32];
    char line[1024];

    if (pid == 0) {
        snprintf(filename, sizeof(filename), "/proc/self/maps");
    } else {
        snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    }//filename="/proc/(pid/self)/maps"

    fp = fopen(filename, "r");//以“读”方式打开maps文件

    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {//逐行读取maps文件中内容至line中
            if (strstr(line, module_name)) {//如果module_name是line之子串
                pch = strtok( line, "-" );
                addr = strtoul( pch, NULL, 16 );//此两句见hook3

                if (addr == 0x8000)
                    addr = 0;

                break;
            }
        }

        fclose(fp) ;
    }

    return (void *)addr;
}


long get_remote_addr(pid_t target_pid, const char* module_name, void* local_addr)
{
    void* local_handle, *remote_handle;

    local_handle = get_module_base(0, module_name);//获取libc.so之本地基址
    remote_handle = get_module_base(target_pid, module_name);//获取目标pid之libc.so基址

    long ret_addr = (long)((uint32_t)local_addr + (uint32_t)remote_handle - (uint32_t)local_handle);
    //远程地址=远程基址+本地地址-本地基址，由于：远程地址-远程基址=本地地址-本地基址=目标函数偏移地址

    return ret_addr;
}


int ptrace_call(pid_t pid, uint32_t addr, long *params, uint32_t num_params, struct pt_regs* regs)
{	//pid:目标pid
	//addr：目标函数地址
	//params：参数数组指针
	//num_params：参数个数
	//regs：调用前寄存器状态
    uint32_t i;
    for (i = 0; i < num_params && i < 4; i ++) {//参数不足4个或大于4个
        regs->uregs[i] = params[i];
    }

    //
    // push remained params onto stack
    //
    if (i < num_params) {//说明i < 4不满足，即i=4
        regs->ARM_sp -= (num_params - i) * sizeof(long) ;//为剩余参数分配空间
        putdata(pid, regs->ARM_sp, (char*)&params[i], (num_params - i) * sizeof(long));
    }

    regs->ARM_pc = addr;
    if (regs->ARM_pc & 1) {
    	/*BX{cond}     Rm   ,指令功能，BX指令跳转到Rm指定的地址去执行程序，
    	    	 * 若Rm的bit0为1， 则跳转时自动将CPSR中的标志T置位，即把目标地址的代码解释为Thumb代码，
    	    	 * 如果为bit0位为0的话， 则跳转时自动将CPSR中的标志T复位，即把目标地址的代码解释为ARM代码。*/
        /* thumb */
        regs->ARM_pc &= (~1u);
        regs->ARM_cpsr |= CPSR_T_MASK;
    } else {
        /* arm */
        regs->ARM_cpsr &= ~CPSR_T_MASK;
    }

    regs->ARM_lr = 0;
    
    if (ptrace_setregs(pid, regs) == -1
            || ptrace_continue(pid) == -1) {
        printf("error\n");
        return -1;
    }

    int stat = 0;
    waitpid(pid, &stat, WUNTRACED);
    while (stat != 0xb7f) {
    	/*通过看ndk的源码sys/wait.h以及man waitpid可以知道这个0xb7f的具体作用。
    	    	 * 首先说一下stat的值：高2字节用于表示导致子进程的退出或暂停状态信号值，
    	    	 * 低2字节表示子进程是退出(0x0)还是暂停(0x7f)状态。0xb7f就表示子进程为暂停状态，
    	    	 * 导致它暂停的信号量为11即sigsegv错误。*/
        if (ptrace_continue(pid) == -1) {
            printf("error\n");
            return -1;
        }
        waitpid(pid, &stat, WUNTRACED);
    }

    return 0;
}


void injectSo(pid_t pid,char* so_path, char* function_name,char* parameter)
{
    struct pt_regs old_regs,regs;
    //pt_regs之定义见：H:\Android\NDK\r10e\platforms\android-21\arch-arm\usr\include\asm\ptrace.h
    long mmap_addr, dlopen_addr, dlsym_addr, dlclose_addr;
    /*逻辑大概如下：保存当前寄存器的状态 -> 获取目标程序的mmap, dlopen, dlsym, dlclose 地址 ->
     * 调用mmap分配一段内存空间用来保存参数信息 –> 调用dlopen加载so文件 -> 调用dlsym找到目标函数地址 ->
     * 使用ptrace_call执行目标函数 -> 调用 dlclose 卸载so文件 -> 恢复寄存器的状态。*/

//save old regs

    ptrace(PTRACE_GETREGS, pid, NULL, &old_regs);//获取目标pid之寄存器值存入old_regs中
    memcpy(&regs, &old_regs, sizeof(regs));//旧寄存器值保存在regs中
    //void *memcpy(void *dest, const void *src, size_t n);
    //size_t：size_t是标准C库中定义的，应为unsigned int，在64位系统中为 long unsigned int

//get remote addres

    printf("getting remote addres:\n");
    mmap_addr = get_remote_addr(pid, libc_path, (void *)mmap);
    dlopen_addr = get_remote_addr( pid, libc_path, (void *)dlopen );
    dlsym_addr = get_remote_addr( pid, libc_path, (void *)dlsym );
    dlclose_addr = get_remote_addr( pid, libc_path, (void *)dlclose );
    
    printf("mmap_addr=%p dlopen_addr=%p dlsym_addr=%p dlclose_addr=%p\n",
    (void*)mmap_addr,(void*)dlopen_addr,(void*)dlsym_addr,(void*)dlclose_addr);
    //获取并打印远程目标函数地址
    
    
    long parameters[10];

//mmap
//void* mmap(void* start,size_t length,int prot,int flags,int fd,off_t offset);
/*start：映射区的开始地址，设置为0时表示由系统决定映射区的起始地址。
 *length：映射区的长度。
 *prot：期望的内存保护标志，不能与文件的打开模式冲突。我们这里设置为RWX。
 *flags：指定映射对象的类型，映射选项和映射页是否可以共享。
 *我们这里设置为：MAP_ANONYMOUS(匿名映射，映射区不与任何文件关联)，
 *				MAP_PRIVATE(建立一个写入时拷贝的私有映射。内存区域的写入不会影响到原文件)。
 *fd：有效的文件描述词。匿名映射设置为0。
 *offset：被映射对象内容的起点。设置为0。*/
    parameters[0] = 0; //address
    parameters[1] = 0x4000; //size
    parameters[2] = PROT_READ | PROT_WRITE | PROT_EXEC; //RWX
    parameters[3] = MAP_ANONYMOUS | MAP_PRIVATE; //flag
    parameters[4] = 0; //fd
    parameters[5] = 0; //offset
    
    ptrace_call(pid, mmap_addr, parameters, 6, &regs);
    ptrace(PTRACE_GETREGS, pid, NULL, &regs);

    long map_base = regs.ARM_r0;
    printf("map_base = %p\n", (void*)map_base);

//dlopen

    printf("save so_path = %s to map_base = %p\n", so_path, (void*)map_base);
    putdata(pid, map_base, so_path, strlen(so_path) + 1);

    parameters[0] = map_base;
    parameters[1] = RTLD_NOW| RTLD_GLOBAL;

    ptrace_call(pid, dlopen_addr, parameters, 2, &regs);
    ptrace(PTRACE_GETREGS, pid, NULL, &regs);
    
    long handle = regs.ARM_r0;
    
    printf("handle = %p\n",(void*) handle);

//dlsym

    printf("save function_name = %s to map_base = %p\n", function_name, (void*)map_base);
    putdata(pid, map_base, function_name, strlen(function_name) + 1);

    parameters[0] = handle;
    parameters[1] = map_base;

    ptrace_call(pid, dlsym_addr, parameters, 2, &regs);
    ptrace(PTRACE_GETREGS, pid, NULL, &regs);
    
    long function_ptr = regs.ARM_r0;

    printf("function_ptr = %p\n", (void*)function_ptr);

//function_call

    printf("save parameter = %s to map_base = %p\n", parameter, (void*)map_base);
    putdata(pid, map_base, parameter, strlen(parameter) + 1);

    parameters[0] = map_base;

    ptrace_call(pid, function_ptr, parameters, 1, &regs);

//dlcose

    parameters[0] = handle;

    ptrace_call(pid, dlclose_addr, parameters, 1, &regs);
    
//restore old regs

    ptrace(PTRACE_SETREGS, pid, NULL, &old_regs);
}


int main(int argc, char *argv[])
{
    if(argc != 2) {//参数个数不为2，则提示用法
        printf("Usage: %s <pid to be traced>\n", argv[0]);
        return 1;
    }
                                                                                                     
    pid_t pid;
    int status;
    pid = atoi(argv[1]);//将待hook的进程pid由字符型转为整型
    
    if(0 != ptrace(PTRACE_ATTACH, pid, NULL, NULL))//附加至pid，附加成功返回0，否则打印错误号后返回
    {
        printf("Trace process failed:%d.\n", errno);
        return 1;
    }
    
    char* so_path = "/data/local/tmp/libinject.so";//待动态加载的so路径
    char* function_name = "mzhengHook";
    char* parameter = "sevenWeapons";
    injectSo(pid, so_path, function_name, parameter);//利用Ptrace动态加载so并执行自定义函数
    
    ptrace(PTRACE_DETACH, pid, NULL, 0);//断开附加
    
    return 0;
}
