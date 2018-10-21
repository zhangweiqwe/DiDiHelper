#include <unistd.h>
#include <stdio.h>
#include <android/log.h>
#include <elf.h>
#include <dlfcn.h>
#include "include/test.h"

#define LOG_TAG "Share"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)

long hookLoad(long l) {
    long a = l + 2;
    //so函数，打印so之pid与传入的字符串
    printf("mzheng Hook pid = %d\n", getpid());
    printf("Hello %s\n", "fsdaf");
    LOGD("mzheng Hook pid = %d\n", getpid());
    LOGD("Hello %s\n", "fdsafasdfasdffd");
    LOGD("Hello %ld\n", a);
    return a;
}

