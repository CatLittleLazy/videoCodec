package com.youmehe.mediotry;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by youmehe on 5/12/21 12:01 AM description:
 */

class Utils {
  public static String TAG = "Utils";

  public static List<String> getAllCodec(int kind) {
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
    String type = kind == 0 ? "REGULAR_CODECS" : "ALL_CODECS";
    String totalInfo =
        "kind == " + type + "\nYour phone have " + allCodecs.length + " codecs = enCodec ("
            + enCodec + ") + " + "deCodec (" + deCodec + "), and support " + supportTypes.size()
            + " types as below\n";
    //+ Arrays.toString(
    //new List[] {supportTypes});
    Log.i(TAG, totalInfo);
    supportTypes.add(0, totalInfo);
    return supportTypes;
  }
}
