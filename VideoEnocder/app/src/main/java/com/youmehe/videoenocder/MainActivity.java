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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkPermission();
    this.mediaProjectionManager =
        (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
    findViewById(R.id.projection).setOnClickListener((view) -> {
      startActivityForResult(captureIntent, 100);
    });
    findViewById(R.id.testRecorderMPEG2TS).setOnClickListener((view) -> {
      startActivity(new Intent(MainActivity.this, CtsRecoderMpeg2Ts.class));
    });

    findViewById(R.id.getThePhoneMediaCodec).setOnClickListener((view) -> {
      startActivity(new Intent(MainActivity.this, MediaCodecActivity.class));
    });
  }

  private void initMediaCodec() {
    try {
      mediaCodec = MediaCodec.createEncoderByType("video/avc");
      MediaFormat mediaFormat =
          MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_SCRAMBLED, 176, 144);
      mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
      //2秒中1个I帧
      mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
      mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
          MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
      mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 400_000);
      mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
      Surface surface = mediaCodec.createInputSurface();
      new Thread(() -> {
        mediaCodec.start();
        mediaProjection.createVirtualDisplay("screen-codec", 540, 960, 1,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
        while (true) {
          MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
          int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
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
      initMediaCodec();
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
          new FileOutputStream(Environment.getExternalStorageDirectory() + "/3gpp.h263", true);
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