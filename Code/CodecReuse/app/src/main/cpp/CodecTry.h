//
// Created by root on 2023/4/5.
//

#ifndef CODECREUSE_CODECTRY_H
#define CODECREUSE_CODECTRY_H
#include <android/log.h>
#define TAG "CodecTry"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#include <memory>

class CodecTry {
public:
    CodecTry()
    {
    }
    CodecTry(const CodecTry &) = delete;
    CodecTry &operator=(const CodecTry &) = delete;
    static std::shared_ptr<CodecTry> getInstance();
    void extractor(int fd);
    void codecOnce();
    void doCodecWork();
    void setWindow(ANativeWindow *);
    ANativeWindow* getWindow() const;
    AMediaCodec *codec = nullptr;
    AMediaExtractor *ex;
    ANativeWindow *window;
    bool sawInputEOS = false;
    bool sawOutputEOS = false;
    bool isPlaying = false;
    bool renderonce;
private:
};


#endif //CODECREUSE_CODECTRY_H
