#include <stdio.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <string.h>
#include "include/config.h"
#include "include/injector.h"
#include "include/utils.h"
#include "include/ptrace.h"
#include "include/test.h"
void* get_module_base(pid_t pid, const char* module_name)
{
    FILE *fp;
    long addr = 0;
    char *pch;
    char filename[32];
    char line[1024];

    if (pid < 0) {
        /* self process */
        snprintf(filename, sizeof(filename), "/proc/self/maps", pid);
    } else {
        snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    }

    fp = fopen(filename, "r");

    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, module_name)) {
                //分解字符串为一组字符串。line为要分解的字符串，"-"为分隔符字符串。
                pch = strtok( line, "-" );
                //将参数pch字符串根据参数base(表示进制)来转换成无符号的长整型数 
                addr = strtoull( pch, NULL, 16 );

                if (addr == 0x8000)
                    addr = 0;

                break;
            }
        }

        fclose(fp) ;
    }

    return (void *)addr;
}

long CallMyFunction(pid_t pid, long so_handle) {


    if (DEBUG) {
        printf("begin-->\n");
    }

    long function_addr = GetRemoteFuctionAddr(pid, REMOTE_CUSTOM_LIB_PATH,
                                              ((long) (void *) hookLoad));
    long params[1];
    params[0] = 12;
    if (DEBUG) {
        printf("CallMyFunction called, function address %lx process %d so handle %lx\n",
               function_addr,
               pid, so_handle);
    }
    long ARM_r0 = CallRemoteFunction(pid, function_addr, params, 1);
    if (DEBUG) {
        printf("end-->%ld\n", ARM_r0);
    }

    return ARM_r0;
}

int main(int argc, char const *argv[]) {
    if (argc != 3) {
        printf("Usage: %s [process name] [library path]\n", argv[0]);
        return -1;
    }

/*
    long long addr0 = 0;
    void (*func0)(int);
    char *path0 = "/data/local2/share.so";
    void *handle0 = dlopen(path0, RTLD_NOW);
    if (handle0 == NULL) {
        printf("Failed to open libaray ");
        fprintf(stderr, "Failed to open libaray %s error:%s\n", path0, dlerror());
    }
    addr0 = (long long) dlsym(handle0, "hookLoad");
    //func0 = addr;
    //func0(27);*/



    const char *process_name = argv[1];
    const char *library_path = argv[2];
    pid_t pid = GetPid(process_name);
    if (DEBUG) {
        printf("process name: %s, library path: %s, pid: %d\n", process_name, library_path, pid);
    }

    if (IsSelinuxEnabled()) {
        DisableSelinux();
    }

    long so_handle = InjectLibrary(pid, library_path);


    long long addr = 0;
    void (*func)(char*);
    addr = (long long)dlsym((void *)so_handle, "hook_entry");
    func = addr;
    if(DEBUG){
        printf("func%lld so_handle%ld",addr,so_handle);
    }

/*
    void* local_handle, *remote_handle;
    local_handle = get_module_base(-1, library_path);
    //获取远程pid的某个模块的起始地址
    remote_handle = get_module_base(pid, library_path);

    if(DEBUG){
        printf("local_handle %ld remote_handle%ld\n",local_handle,remote_handle);
    }
    void * ret_addr = (void *)((uintptr_t)hookLoad + (uintptr_t)remote_handle - (uintptr_t)local_handle);

    long long addr = 0;
    void (*func)(char*);
    func = (long long)&ret_addr;
    if(DEBUG){
        printf("ret_addr %ld",ret_addr);
    }
    func(1);*/


    //func("12");

    /*long long addr = 0;
    void (*func)(int);
    //addr = (void *)dlsym(so_handle, "hookLoad");
    addr = (long long)dlsym(so_handle, "hookLoad2");
    func = addr;
    if(DEBUG){
        printf("func%lld so_handle%ld",addr,so_handle);
    }*/
    //func(1);

    //CallMyFunction(pid, so_handle);

    return 0;
}


