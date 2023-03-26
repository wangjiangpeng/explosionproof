/*
 * log.h
 *
 *  Created on: 2015��6��24��
 *      Author: Administrator
 */

#ifndef LOG_H_
#define LOG_H_
#include <jni.h>
#include <android/log.h>
#define DEBUG
#ifdef DEBUG
#define   LOG_TAG    "J_DEVICE"
#define   LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define   LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define   LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define   LOGD(...)
#define   LOGI(...)
#define   LOGE(...)
#endif


#endif /* LOG_H_ */
