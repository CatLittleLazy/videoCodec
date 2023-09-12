package com.youmehe.mediatry;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by youmehe on 5/12/21 12:01 AM description:
 */

class Utils {
  public static String TAG = "Utils";

  public static List<String> getAllCodec(int kind) throws IOException {
    MediaCodecList mediaCodecList = new MediaCodecList(kind);
    MediaCodecInfo[] allCodecs = mediaCodecList.getCodecInfos();
    MediaCodec mediaCodec = null;
    // allCodecswill not be null and foreach will check length auto
    int enCodec = 0;
    int deCodec = 0;
    List<String> supportTypes = new ArrayList<>();
    for (MediaCodecInfo mediaCodecInfo : mediaCodecList.getCodecInfos()) {
      Log.e(TAG, mediaCodecInfo.getName());
      if (mediaCodecInfo.isEncoder()) {
        enCodec++;
      } else {
        deCodec++;
      }
      for (String type : mediaCodecInfo.getSupportedTypes()) {
        if (!supportTypes.contains(type)) {
          Log.e(TAG, type + "_" + type.contains("video"));
          if (type.contains("video")) {
            if (mediaCodecInfo.isEncoder()) {
              mediaCodec = MediaCodec.createEncoderByType(type);
            } else {
              mediaCodec = MediaCodec.createDecoderByType(type);
            }
            List<MediaCodecInfo.VideoCapabilities.PerformancePoint> pps = mediaCodec.getCodecInfo()
                .getCapabilitiesForType(type).getVideoCapabilities()
                .getSupportedPerformancePoints();
            if (pps != null) {
              Log.e(TAG, "?--" + Arrays.toString(pps.toArray()));
              for (MediaCodecInfo.VideoCapabilities.PerformancePoint cpp : pps) {
                //this is a testApi,we can not use;use reflect can not get also
                Object obj = cpp.getClass();
                try {
                  Method method = ((Class<?>) obj).getMethod("toString");
                  String m = (String) method.invoke(cpp);
                  Log.e(TAG, "?-----------------------" + m);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                  e.printStackTrace();
                }
              }
            }
          }
          supportTypes.add(type);
        }
      }
      Log.e(TAG, Arrays.toString(mediaCodecInfo.getSupportedTypes()));
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
    }
    String type = kind == 0 ? "REGULAR_CODECS" : "ALL_CODECS";
    String totalInfo =
        "kind == " + type + "\nYour phone have " + result.size() + " codecs = enCodec (" + enCodec
            + ") + " + "deCodec (" + deCodec + ")\n";
    result.add(0, totalInfo);
    return result;
  }

  public static void getVideoCapabilitiesTest(MediaCodec codec) {
    //MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/x-vnd.on2.vp9", 3840, 2160);
    //mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 1);
    MediaCodecInfo mediaCodecInfo = codec.getCodecInfo();

    List<MediaCodecInfo.VideoCapabilities.PerformancePoint> pps = mediaCodecInfo
        .getCapabilitiesForType("video/x").getVideoCapabilities()
        .getSupportedPerformancePoints();
    Log.e(TAG, "?--" + Arrays.toString(pps.toArray()));
  }

  /**
   * //from cts CodecPerformanceTestBase.java public static int getMaxOperatingRate(String
   * codecName, String mime, int width, int height, int frameRate) throws Exception { MediaCodec
   * codec = MediaCodec.createByCodecName(codecName); MediaCodecInfo mediaCodecInfo =
   * codec.getCodecInfo(); List<MediaCodecInfo.VideoCapabilities.PerformancePoint> pps =
   * mediaCodecInfo .getCapabilitiesForType(mime).getVideoCapabilities()
   * .getSupportedPerformancePoints(); if (pps.size() <= 0) { return 0; }
   * MediaCodecInfo.VideoCapabilities.PerformancePoint cpp = new MediaCodecInfo.VideoCapabilities.PerformancePoint(width,
   * height, frameRate);
   * <p>
   * //int macroblocks = cpp.getMaxMacroBlocks(); //int maxOperatingRate = -1; //for
   * (MediaCodecInfo.VideoCapabilities.PerformancePoint pp : pps) { //  if (pp.covers(cpp)) { //
   * maxOperatingRate = Math.max(Math.min(pp.getMaxFrameRate(), //        (int)
   * pp.getMaxMacroBlockRate() / macroblocks), maxOperatingRate); //  } //} //codec.release();
   * //assertTrue(maxOperatingRate != -1); return -1; }
   */

  public static void logLine(String tag) {
    Log.e(tag, "-------------------------------------------------------");
  }
}
