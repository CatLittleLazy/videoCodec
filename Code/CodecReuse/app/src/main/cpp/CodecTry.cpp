//
// Created by root on 2023/4/5.
//

#include <media/NdkMediaFormat.h>
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaExtractor.h>
#include <android/native_window.h>
#include "CodecTry.h"
static std::shared_ptr<CodecTry> single;

std::shared_ptr<CodecTry> CodecTry::getInstance() {
    if (single != nullptr)
    {
        return single;
    }
    single = std::shared_ptr<CodecTry>(new CodecTry);
    return single;
}

void CodecTry::extractor(int fd) {
    if (fd < 0) {
        LOGE("failed to open file:%d", fd);
        return;
    }

//    data.fd = fd;
//
//    workerdata *d = &data;

    ex = AMediaExtractor_new();
    media_status_t err = AMediaExtractor_setDataSourceFd(
            ex, fd, static_cast<off64_t>(0), static_cast<off64_t>(0xffffffffff));

    if (err != AMEDIA_OK) {
        LOGV("setDataSource error: %d", err);
        return;
    } else {
        LOGV("setDataSource success");
    }

    int numtracks = AMediaExtractor_getTrackCount(ex);

    LOGV("input has %d tracks", numtracks);
    for (int i = 0; i < numtracks; i++) {
        AMediaFormat *format = AMediaExtractor_getTrackFormat(ex, i);
        const char *s = AMediaFormat_toString(format);
//        LOGV("track %d format: %s", i, s);
        const char *mime;
        if (!AMediaFormat_getString(format, AMEDIAFORMAT_KEY_MIME, &mime)) {
            LOGV("no mime type");
            return;
        } else if (!strncmp(mime, "video/", 6)) {
            LOGV("track %d format: %s", i, s);
            // Omitting most error handling for clarity.
            // Production code should check for errors.
            AMediaExtractor_selectTrack(ex, i);
            if (codec == nullptr) {
                LOGV("create codec");
                codec = AMediaCodec_createDecoderByType(mime);
            }
//            AMediaCodec_configure(codec, format, window, NULL, 0);
//            if (!isPlaying) {
            int result = ANativeWindow_setBuffersGeometry(window, 200, 200, AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM);
            ANativeWindow_acquire(window);
            int32_t rotation = -1;
            bool ret = AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_ROTATION, &rotation);
            LOGV("result is %d, ret = %d, rotation = %d", result, ret, rotation);
            if (rotation == -1) {
                AMediaFormat_setInt32(format, AMEDIAFORMAT_KEY_ROTATION, 0);
            }
            AMediaFormat_setInt32(format, "android._video-scaling", 1);

            media_status_t status = AMediaCodec_configure(codec, format, window, nullptr, 0);
                if (status == AMEDIA_OK) {
                    LOGV("configure 成功");
                }
//            }
//            ex = ex;
//            codec = codec;
//            renderstart = -1;
//            sawInputEOS = false;
//            sawOutputEOS = false;
//            isPlaying = false;
//            renderonce = true;
//            if (!isPlaying) {
                LOGV("codec start");
                AMediaCodec_start(codec);
//                isPlaying = true;
//            }
        }
        AMediaFormat_delete(format);
    }
}

void CodecTry::codecOnce() {
    ssize_t bufidx = -1;
    if (!sawInputEOS) {
        bufidx = AMediaCodec_dequeueInputBuffer(codec, 2000);
        LOGV("input buffer %zd", bufidx);
        if (bufidx >= 0) {
            size_t bufsize;
            auto buf = AMediaCodec_getInputBuffer(codec, bufidx, &bufsize);
            auto sampleSize = AMediaExtractor_readSampleData(ex, buf, bufsize);
            if (sampleSize < 0) {
                sampleSize = 0;
                sawInputEOS = true;
                LOGV("EOS");
            }
            auto presentationTimeUs = AMediaExtractor_getSampleTime(ex);

            AMediaCodec_queueInputBuffer(
                    codec, bufidx, 0, sampleSize, presentationTimeUs,
                    sawInputEOS ? AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM : 0);
            AMediaExtractor_advance(ex);
        }
    }

    if (!sawOutputEOS) {
        AMediaCodecBufferInfo info;
        auto status = AMediaCodec_dequeueOutputBuffer(codec, &info, 0);
        if (status >= 0) {
            if (info.flags & AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM) {
                LOGV("output EOS");
                sawOutputEOS = true;
            }
//            int64_t presentationNano = info.presentationTimeUs * 1000;
//            if (renderstart < 0) {
//                renderstart = systemnanotime() - presentationNano;
//            }
//            int64_t delay = (renderstart + presentationNano) - systemnanotime();
//            if (delay > 0) {
                //usleep(delay / 1000);
//            }
            LOGV("info size is %d",info.size);
            media_status_t err = AMediaCodec_releaseOutputBuffer(codec, status, info.size != 0);
//            media_status_t err = AMediaCodec_releaseOutputBuffer(codec, status, false);
            if (err == AMEDIA_OK) {
                LOGV("release codec --> %lld", info.presentationTimeUs);
            }
            if (renderonce) {
                renderonce = false;
                return;
            }
        } else if (status == AMEDIACODEC_INFO_OUTPUT_BUFFERS_CHANGED) {
            LOGV("output buffers changed");
        } else if (status == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
            auto format = AMediaCodec_getOutputFormat(codec);
            LOGV("wyt format changed to: %s", AMediaFormat_toString(format));
            AMediaFormat_setInt32(format, "android._video-scaling", 1);
            AMediaFormat_delete(format);
        } else if (status == AMEDIACODEC_INFO_TRY_AGAIN_LATER) {
            LOGV("no output buffer right now");
        } else {
            LOGV("unexpected info code: %zd", status);
        }
    }

//    if (!sawInputEOS || !sawOutputEOS) {
//        mlooper->post(kMsgCodecBuffer, d);
//    }
}

void CodecTry::doCodecWork() {
    LOGV("doCodecWork start");
    int i = 0;
//    while(i < 100) {
    while(!sawOutputEOS) {
//        i ++;
        codecOnce();
    }
    sawInputEOS = false;
    sawOutputEOS = false;
    AMediaCodec_flush(codec);
    AMediaCodec_stop(codec);
    LOGV("doCodecWork done");
}

void CodecTry::setWindow(ANativeWindow * aNativeWindow) {
    window = aNativeWindow;
}

ANativeWindow *CodecTry::getWindow() const {
    return window;
}
