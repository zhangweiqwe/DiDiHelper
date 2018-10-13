#include <jni.h>
#include <android/log.h>
#include <string>

#include <stdio.h>
#include <sys/ptrace.h>
#include <sys/wait.h>

#define LOG_TAG "native-lib"
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_cn_zr_Other_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {


    uint8_t data;
    int stat;
    int pid = 32340;//atoi() ;
    ptrace(PTRACE_ATTACH, pid, NULL, NULL);
    wait(&stat);    // 如果不wait，马上进行下一个ptrace的PEEK操作会造成 no such process 错误
    int addr = 0x12c000000;
    for (; addr < 0x52c000000; ++addr) {
        data = ptrace(PTRACE_PEEKDATA, pid, addr, NULL);    // 一次读一个字节
        /*if (data == 0x17 || data == 0xce) {
            printf("data = %x , addr = %x\n", data, addr);
        }*/
        LOG_D("data = %x , addr = %x\n", data, addr);
    }

    ptrace(PTRACE_DETACH, pid, NULL, NULL);

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());


/*
    uint8_t data ;
    int stat ;
    int pid = 23249;//atoi() ;
    ptrace(PTRACE_ATTACH, pid, NULL, NULL) ;
    wait(&stat) ;    // 如果不wait，马上进行下一个ptrace的PEEK操作会造成 no such process 错误
    int addr = 0x00009000 ;
    for (; addr < 0x0000a000; ++addr)
    {
        data = ptrace(PTRACE_PEEKDATA, pid, addr, NULL);    // 一次读一个字节
        if(data == 0x17 || data == 0xce){
            printf("data = %x , addr = %x\n" , data , addr) ;
        }
    }

    ptrace(PTRACE_DETACH, pid, NULL, NULL);

    return 1 ;*/

}