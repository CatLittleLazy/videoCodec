package com.youmehe.mediotry;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2021/2/11 10:14 description:
 */
class MyH264Player implements Runnable {

  String path;
  Surface surface;
  MediaCodec mediaCodec;
  Context context;

  public MyH264Player(Context context, String path, Surface surface) {
    this.path = path;
    this.surface = surface;
    this.context = context;
    try {
      mediaCodec = MediaCodec.createDecoderByType("video/hevc");
      MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/hevc", 368, 384);
      mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
      //编辑的时候 生成  --> surface
      mediaCodec.configure(mediaFormat, surface, null, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void play() {
    mediaCodec.start();
    new Thread(this).start();
  }

  @Override public void run() {
    try {
      decodeH264();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void decodeH264() {
    byte[] bytes = null;
    Log.e("test", path);
    try {
      bytes = getBytes(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //所有队列
    ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    int startIndex = 0;
    int nextFrameStart = 0;
    int totalSize = bytes.length;
    while (true) {
      if (startIndex >= totalSize) {
        break;
      }
      nextFrameStart = findByFrame(bytes, startIndex );
      int inIndex = mediaCodec.dequeueInputBuffer(10000);
      if (inIndex >= 0) {
        ByteBuffer byteBuffer = inputBuffers[inIndex];
        byteBuffer.clear();
        byteBuffer.put(bytes, startIndex, nextFrameStart - startIndex);
        //通知DSP芯片解码
        mediaCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
        //为下一帧做准备
        startIndex = nextFrameStart;
      } else {
        continue;
      }

      int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
      Log.e("test", outIndex + "---?");
      if (outIndex >= 0) {
        try {
          // 音视频同步  ---> 33ms  =  解码时间 +  渲染时间 + 等待时间
          Thread.sleep(33);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        mediaCodec.releaseOutputBuffer(outIndex, true);
      } else {
        Log.e("test", "解码失败");
      }
    }
  }

  private int findByFrame(byte[] bytes, int startIndex) {
    int totalSize = bytes.length;
    for (int i = startIndex; i < totalSize; i++) {
      if (bytes[i] == 0x00
          && bytes[i + 1] == 0x00
          && bytes[i + 2] == 0x00
          && bytes[i + 3] == 0x01) {
        return i;
      }
    }
    return -1;
  }

  public byte[] getBytes(String path) throws IOException {
    Log.e("test", "getBytes");
    InputStream is = new DataInputStream(new FileInputStream(new File(path)));
    Log.e("test", "getBytes--");
    int len;
    int size = 1024;
    byte[] buf;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    buf = new byte[size];
    while ((len = is.read(buf, 0, size)) != -1) {
      bos.write(buf, 0, len);
    }
    buf = bos.toByteArray();
    Log.e("test", buf.length + "");
    return buf;
  }
}
