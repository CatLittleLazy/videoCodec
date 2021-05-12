package com.youmehe.mediotry;

import android.media.MediaCodec;
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

  public static List<Object> getAllCodecInfo(int kind) {
    MediaCodecList mediaCodecList = new MediaCodecList(kind);
    MediaCodecInfo[] allCodecs = mediaCodecList.getCodecInfos();
    // allCodecswill not be null and foreach will check length auto
    int enCodec = 0;
    int deCodec = 0;
    List<Object> result = new ArrayList<>();
    for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
      if (mediaCodecInfo.isAlias()) {
        continue;
      }

      result.add(mediaCodecInfo);
      Log.e(TAG, "name -> " + mediaCodecInfo.getName());
      Log.e(TAG, "isSoftwareOnly -> " + mediaCodecInfo.isSoftwareOnly());
      Log.e(TAG, "isHardwareAccelerated -> " + mediaCodecInfo.isHardwareAccelerated());
      Log.e(TAG, "isVendor -> " + mediaCodecInfo.isVendor());
      Log.e(TAG, "supported types -> " + Arrays.toString(mediaCodecInfo.getSupportedTypes()));
      if (mediaCodecInfo.isEncoder()) {
        enCodec++;
      } else {
        deCodec++;
      }
      logLine(TAG);
      MediaCodecInfo.VideoCapabilities.PerformancePoint p = getSupportedPerformancePoints();
      mediaCodecInfo.getCapabilitiesForType()
    }
    String type = kind == 0 ? "REGULAR_CODECS" : "ALL_CODECS";
    String totalInfo =
        "kind == " + type + "\nYour phone have " + result.size() + " codecs = enCodec (" + enCodec
            + ") + " + "deCodec (" + deCodec + ")\n";
    result.add(0, totalInfo);
    return result;
  }

  //from cts CodecPerformanceTestBase.java
  int getMaxOperatingRate(String codecName, String mime) throws Exception {
    MediaCodec codec = MediaCodec.createByCodecName(codecName);
    MediaCodecInfo mediaCodecInfo = codec.getCodecInfo();
    List<MediaCodecInfo.VideoCapabilities.PerformancePoint> pps = mediaCodecInfo
        .getCapabilitiesForType(mime).getVideoCapabilities()
        .getSupportedPerformancePoints();
    assertTrue(pps.size() > 0);
    MediaCodecInfo.VideoCapabilities.PerformancePoint cpp =
        new MediaCodecInfo.VideoCapabilities.PerformancePoint(mWidth, mHeight, mFrameRate);
    int macroblocks = cpp.getMaxMacroBlocks();
    int maxOperatingRate = -1;
    for (MediaCodecInfo.VideoCapabilities.PerformancePoint pp : pps) {
      if (pp.covers(cpp)) {
        maxOperatingRate = Math.max(Math.min(pp.getMaxFrameRate(),
            (int) pp.getMaxMacroBlockRate() / macroblocks), maxOperatingRate);
      }
    }
    codec.release();
    assertTrue(maxOperatingRate != -1);
    return maxOperatingRate;
  }

  public static void logLine(String tag) {
    Log.e(tag, "-------------------------------------------------------");
  }
}
