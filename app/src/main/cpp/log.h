#ifndef LOG_H_
#define LOG_H_

#include <android/log.h>

#define ENABLE_DEBUG 1

#if ENABLE_DEBUG
#define  LOG_TAG "INJECT"
#define  LOGD(fmt, args...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
// #define DEBUG_PRINT(format,args...) \
    // LOGD(format, ##args)
#define DEBUG_PRINT(...)    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
// #define DEBUG_PRINT(format,args...)
#define DEBUG_PRINT(...)
#endif

#endif