package com.youmehe.videoenocder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

  //数据源 一 摄像头
  //数据源 二 屏幕

  private MediaProjection mediaProjection;
  private MediaProjectionManager mediaProjectionManager;
  private MediaCodec mediaCodec;
  VideoView videoView;
  VideoEncoder videoEncoder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkPermission();
    videoView = findViewById(R.id.videoview);
    this.mediaProjectionManager =
        (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
    findViewById(R.id.projection).setOnClickListener((view) -> {
      startActivityForResult(captureIntent, 100);
      mediaPlay("VID_20230717_003133.mp4");
    });
    findViewById(R.id.testRecorderMPEG2TS).setOnClickListener((view) -> {
      startActivity(new Intent(MainActivity.this, CtsRecoderMpeg2Ts.class));
    });
    findViewById(R.id.stop_encoder).setOnClickListener((view) -> {
      videoEncoder.release();
      new Thread(()->{
        try {
          Thread.sleep(1100);
          runOnUiThread(()->{
            mediaPlay("codec.mp4");
          });
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }).start();
    });

    String mimeType = "video/hevc";
    MediaCodecInfo codecInfo = null;
    for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
      MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
      if (!info.isEncoder()) {
        continue;
      }
      String[] types = info.getSupportedTypes();
      for (String type : types) {
        if (type.equalsIgnoreCase(mimeType)) {
          codecInfo = info;
          break;
        }
      }
      if (codecInfo != null) {
        break;
      }
    }
  }

  private void mediaPlay(String fileName) {
    videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/Movies/" + fileName);
    videoView.start();
    videoView.setOnCompletionListener(mediaPlayer -> {
      videoView.start();
    });
  }

  private void initMediaCodec() {
    try {
      mediaCodec = MediaCodec.createEncoderByType("video/avc");
      MediaFormat mediaFormat =
          MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1920, 1080);
      mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
      //2秒中1个I帧
      mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30);
      mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
          MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
      mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 400_000);
      mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
      Surface surface = mediaCodec.createInputSurface();
      new Thread(() -> {
        mediaCodec.start();
        mediaProjection.createVirtualDisplay("screen-codec", 1920, 1080, 1,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
        while (true) {
          MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
          int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
          Log.i("encoder", "run" + outIndex);
          if (outIndex >= 0) {
            // byteBuffer 是压缩数据
            ByteBuffer byteBuffer = mediaCodec.getOutputBuffer(outIndex);
            byte[] outData = new byte[bufferInfo.size];
            byteBuffer.get(outData);
            //以字符串方式写入codec.txt文件
            writeContent(outData);
            //以文件方式写入codec.h264文件
            writeBytes(outData);

            mediaCodec.releaseOutputBuffer(outIndex, true);
          }
        }
      }).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void checkPermission() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] {
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
      }, 1);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 100 && resultCode == RESULT_OK) {
      mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
//      initMediaCodec();
      videoEncoder = new VideoEncoder();
      videoEncoder.init(Environment.getExternalStorageDirectory() + "/Movies/codec.mp4", 1920, 1080, mediaProjection);
    }
  }

  public String writeContent(byte[] array) {
    char[] HEX_CHAR_TABLE =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'E', 'F'};
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : array) {
      stringBuilder.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);
      stringBuilder.append(HEX_CHAR_TABLE[(b & 0x0f)]);
    }
    Log.i("encoder", "writeContent" + stringBuilder.toString());
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(Environment.getExternalStorageDirectory() + "/codec.txt", true);
      fileWriter.write(stringBuilder.toString());
      fileWriter.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fileWriter != null) {
          fileWriter.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return stringBuilder.toString();
  }

  public void writeBytes(byte[] array) {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream =
          new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.mov", true);
      fileOutputStream.write(array);
      fileOutputStream.write('\n');
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}