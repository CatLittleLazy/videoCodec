#include <jni.h>
#include <string>
#include "media/NdkMediaCodec.h"
#include "media/NdkMediaExtractor.h"
#include <android/log.h>
#include "CodecTry.h"
#define TAG "NativeCodec"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_youmehe_codecreuse_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_youmehe_codecreuse_MainActivity_extractFromFd(JNIEnv *env, jobject thiz, jint fd) {
    // TODO: implement extractFromFd()
    LOGV("@@@ create --> %p", CodecTry::getInstance().get());
    CodecTry::getInstance()->extractor(fd);
    CodecTry::getInstance()->doCodecWork();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_youmehe_codecreuse_MainActivity_codecNow(JNIEnv *env, jobject thiz) {
    // TODO: implement codecNow()
}