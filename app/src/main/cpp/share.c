#include <android/log.h>
#include <stdarg.h>
#include <stdio.h>
#include <zconf.h>


#define TAG "Hook Library"
#define LOG_TAG "Debug"
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

int my_printf(long l) {



    LOG_D("---->data = %s<-----12345 %d %ld\n", "in", getpid(),l);
    va_list args;
    /*va_start(args, format);
    int ret = __android_log_vprint(ANDROID_LOG_DEBUG, TAG, format, args);
    va_end(args);*/
    return 12;
}