#include <jni.h>
#include <string>
#include "media/NdkMediaCodec.h"
#include "media/NdkMediaExtractor.h"
#include <android/log.h>
#include <android/native_window_jni.h>
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
extern "C"
JNIEXPORT void JNICALL
Java_com_youmehe_codecreuse_MainActivity_steSurface(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement steSurface()
    // obtain a native window from a Java surface
    if (CodecTry::getInstance()->getWindow()) {
        ANativeWindow_release(CodecTry::getInstance()->getWindow());
        CodecTry::getInstance()->setWindow(nullptr);
    }
    CodecTry::getInstance()->setWindow(ANativeWindow_fromSurface(env, surface));
    LOGV("@@@ setsurface %p", CodecTry::getInstance()->getWindow());
}