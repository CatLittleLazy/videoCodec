package com.youmehe.videoenocder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaCodecActivity extends AppCompatActivity {

  public static String TAG = "MediaCodecActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_media_codec);
    getAllCodec(MediaCodecList.REGULAR_CODECS);
  }

  public void getAllCodec(int kind) {
    MediaCodecList mediaCodecList = new MediaCodecList(kind);
    MediaCodecInfo[] allCodecs = mediaCodecList.getCodecInfos();
    // allCodecswill not be null and foreach will check length auto
    int enCodec = 0;
    int deCodec = 0;
    List<String> supportTypes = new ArrayList<>();
    for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
      Log.e(TAG, mediaCodecInfo.getName());
      for (String type : mediaCodecInfo.getSupportedTypes()) {
        if (!supportTypes.contains(type)) {
          supportTypes.add(type);
        }
      }
      Log.e(TAG, Arrays.toString(mediaCodecInfo.getSupportedTypes()));
      if (mediaCodecInfo.isEncoder()) {
        enCodec++;
      } else {
        deCodec++;
      }
    }
    Log.i(TAG, "Your phone have "
        + allCodecs.length
        + " codecs = enCodec ("
        + enCodec
        + ") + "
        + "deCodec ("
        + deCodec
        + "), and support "
        + supportTypes.size()
        + " types as below \n"
        + Arrays.toString(
        new List[] {supportTypes}));
  }
}