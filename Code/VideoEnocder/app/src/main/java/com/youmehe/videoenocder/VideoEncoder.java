package com.youmehe.videoenocder;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoEncoder {

    private static final String TAG = "VideoEncoder";

//    private final static String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    private final static String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_HEVC;
    private static final long DEFAULT_TIMEOUT_US = 10000;

    private MediaCodec mEncoder;
    private MediaMuxer mMediaMuxer;
    private int mVideoTrackIndex;
    private boolean mStop = false;

    public void init(String outPath, int width, int height, MediaProjection mediaProjection) {
        try {
            mStop = false;
            mVideoTrackIndex = -1;
            mMediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
            // 编码器输入是NV12格式
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 6);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mEncoder.createInputSurface();
            new Thread(()->{
                mEncoder.start();
                mediaProjection.createVirtualDisplay("screen-codec", width, height, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                while (!mStop) {
                    int outputBufferIndex = mEncoder.dequeueOutputBuffer(bufferInfo, DEFAULT_TIMEOUT_US);
                    Log.d(TAG, "outputBufferIndex: " + outputBufferIndex);
                    if (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                        // write head info
                        if (mVideoTrackIndex == -1) {
                            Log.d(TAG, "this is first frame, call writeHeadInfo first");
                            mVideoTrackIndex = writeHeadInfo(outputBuffer, bufferInfo);
                        }
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                            Log.d(TAG, "write outputBuffer");
                            mMediaMuxer.writeSampleData(mVideoTrackIndex, outputBuffer, bufferInfo);
                        }
                        mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        mStop = true;
        new Thread(()->{
            try {
                Thread.sleep(1000);
                if (mEncoder != null) {
                    mEncoder.stop();
                    mEncoder.release();
                    mEncoder = null;
                }
                if (mMediaMuxer != null) {
                    mMediaMuxer.release();
                    mMediaMuxer = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void encode(byte[] yuv, long presentationTimeUs) {
        if (mEncoder == null || mMediaMuxer == null) {
            Log.e(TAG, "mEncoder or mMediaMuxer is null");
            return;
        }
        if (yuv == null) {
            Log.e(TAG, "input yuv data is null");
            return;
        }
        int inputBufferIndex = mEncoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
        Log.d(TAG, "inputBufferIndex: " + inputBufferIndex);
        if (inputBufferIndex == -1) {
            Log.e(TAG, "no valid buffer available");
            return;
        }
        ByteBuffer inputBuffer = mEncoder.getInputBuffer(inputBufferIndex);
        inputBuffer.put(yuv);
        mEncoder.queueInputBuffer(inputBufferIndex, 0, yuv.length, presentationTimeUs, 0);
        while (!mStop) {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(bufferInfo, DEFAULT_TIMEOUT_US);
            Log.d(TAG, "outputBufferIndex: " + outputBufferIndex);
            if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                // write head info
                if (mVideoTrackIndex == -1) {
                    Log.d(TAG, "this is first frame, call writeHeadInfo first");
                    mVideoTrackIndex = writeHeadInfo(outputBuffer, bufferInfo);
                }
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                    Log.d(TAG, "write outputBuffer");
                    mMediaMuxer.writeSampleData(mVideoTrackIndex, outputBuffer, bufferInfo);
                }
                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                break; // 跳出循环
            }
        }
    }

    private static ByteBuffer extractHevcParamSets(byte[] bitstream) {
        final byte[] startCode = {0x00, 0x00, 0x00, 0x01};
        int nalBeginPos = 0, nalEndPos = 0;
        int nalUnitType = -1;
        int nlz = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int pos = 0; pos < bitstream.length; pos++) {
            if (2 <= nlz && bitstream[pos] == 0x01) {
                nalEndPos = pos - nlz;
                if (nalUnitType == 32 || nalUnitType == 33 || nalUnitType == 34) {
                    // extract VPS(32), SPS(33), PPS(34)
                    Log.d(TAG, "NUT=" + nalUnitType + " range={" + nalBeginPos + "," + nalEndPos + "}");
                    try {
                        baos.write(startCode);
                        baos.write(bitstream, nalBeginPos, nalEndPos - nalBeginPos);
                    } catch (IOException ex) {
                        Log.e(TAG, "extractHevcParamSets", ex);
                        return null;
                    }
                }
                nalBeginPos = ++pos;
                nalUnitType = (bitstream[pos] >> 1) & 0x2f;
                if (0 <= nalUnitType && nalUnitType <= 31) {
                    break;  // VCL NAL; no more VPS/SPS/PPS
                }
            }
            nlz = (bitstream[pos] != 0x00) ? 0 : nlz + 1;
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public void searchVpsSpsPpsFromH265(ByteBuffer csd0byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        int vpsPosition = -1;
        int spsPosition = -1;
        int ppsPosition = -1;
        int contBufferInitiation = 0;
        byte[] csdArray = new byte[bufferInfo.size];
        csd0byteBuffer.limit(bufferInfo.offset + bufferInfo.size);
        csd0byteBuffer.position(bufferInfo.offset);
        csd0byteBuffer.get(csdArray);
//        byte[] csdArray = csd0byteBuffer.array();
        for (int i = 0; i < csdArray.length; i++) {
            if (contBufferInitiation == 3 && csdArray[i] == 1) {
                if (vpsPosition == -1) {
                    vpsPosition = i - 3;
                } else if (spsPosition == -1) {
                    spsPosition = i - 3;
                } else {
                    ppsPosition = i - 3;
                }
            }
            if (csdArray[i] == 0) {
                contBufferInitiation++;
            } else {
                contBufferInitiation = 0;
            }
        }
        byte[] vps = new byte[spsPosition];
        byte[] sps = new byte[ppsPosition - spsPosition];
        byte[] pps = new byte[csdArray.length - ppsPosition];
        for (int i = 0; i < csdArray.length; i++) {
            if (i < spsPosition) {
                vps[i] = csdArray[i];
            } else if (i < ppsPosition) {
                sps[i - spsPosition] = csdArray[i];
            } else {
                pps[i - ppsPosition] = csdArray[i];
            }
        }

        Log.d(TAG, "searchVpsSpsPpsFromH265: vps="+ bytesToHex(vps)+",sps="+bytesToHex(sps)+",pps="+bytesToHex(pps));
        //vps=0000000140010C01FFFF016000000300B0000003000003005DAC59,sps=00000001420101016000000300B0000003000003005DA00280802E1F1396BB9324BB948281010176850940,pps=000000014401C0F1800420
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private int writeHeadInfo(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        byte[] csd = new byte[bufferInfo.size];
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
        outputBuffer.position(bufferInfo.offset);
        outputBuffer.get(csd);
        ByteBuffer sps = null;
        ByteBuffer pps = null;
        for (int i = bufferInfo.size - 1; i > 3; i--) {
            if (csd[i] == 1 && csd[i - 1] == 0 && csd[i - 2] == 0 && csd[i - 3] == 0) {
                sps = ByteBuffer.allocate(i - 3);
                pps = ByteBuffer.allocate(bufferInfo.size - (i - 3));
                sps.put(csd, 0, i - 3).position(0);
                pps.put(csd, i - 3, bufferInfo.size - (i - 3)).position(0);
            }
        }
        MediaFormat outputFormat = mEncoder.getOutputFormat();
        if (false) {
            if (sps != null && pps != null) {
                outputFormat.setByteBuffer("csd-0", sps);
                outputFormat.setByteBuffer("csd-1", pps);
            }
        }else {
//            searchVpsSpsPpsFromH265(outputBuffer, bufferInfo);
//            outputFormat.setByteBuffer("csd-0", extractHevcParamSets(outputBuffer.array()));
        }
        Log.e(TAG, "testwyt " + outputFormat.toString());
        int videoTrackIndex = mMediaMuxer.addTrack(outputFormat);
        Log.d(TAG, "videoTrackIndex: " + videoTrackIndex);
        mMediaMuxer.start();
        return videoTrackIndex;
    }
}
