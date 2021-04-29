package com.youmehe.videoenocder;

import android.graphics.Camera;
import android.media.CamcorderProfile;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.util.Set;

public class CtsRecoderMpeg2Ts extends AppCompatActivity {

  private final String TAG = "MediaRecorderTest";
  private String OUTPUT_PATH;
  private String OUTPUT_PATH2;
  private static final float TOLERANCE = 0.0002f;
  private static final int RECORD_TIME_MS = 3000;
  private static final int RECORD_TIME_LAPSE_MS = 6000;
  private static final int RECORD_TIME_LONG_MS = 20000;
  private static final int RECORDED_DUR_TOLERANCE_MS = 1000;
  private static final int TEST_TIMING_TOLERANCE_MS = 70;
  // Tolerate 4 frames off at maximum
  private static final float RECORDED_DUR_TOLERANCE_FRAMES = 4f;
  private static final int VIDEO_WIDTH = 176;
  private static final int VIDEO_HEIGHT = 144;
  private static int mVideoWidth = VIDEO_WIDTH;
  private static int mVideoHeight = VIDEO_HEIGHT;
  private static final int VIDEO_BIT_RATE_IN_BPS = 128000;
  private static final double VIDEO_TIMELAPSE_CAPTURE_RATE_FPS = 1.0;
  private static final int AUDIO_BIT_RATE_IN_BPS = 12200;
  private static final int AUDIO_NUM_CHANNELS = 1;
  private static final int AUDIO_SAMPLE_RATE_HZ = 8000;
  private static final long MAX_FILE_SIZE = 5000;
  private static final int MAX_FILE_SIZE_TIMEOUT_MS = 5 * 60 * 1000;
  private static final int MAX_DURATION_MSEC = 2000;
  private static final float LATITUDE = 0.0000f;
  private static final float LONGITUDE = -180.0f;
  private static final int NORMAL_FPS = 30;
  private static final int TIME_LAPSE_FPS = 5;
  private static final int SLOW_MOTION_FPS = 120;

  private boolean mOnInfoCalled;
  private boolean mOnErrorCalled;
  private File mOutFile;
  private File mOutFile2;
  private Camera mCamera;
  private int mFileIndex;

  private MediaRecorder mMediaRecorder;
  private ConditionVariable mMaxDurationCond;
  private ConditionVariable mMaxFileSizeCond;
  private ConditionVariable mMaxFileSizeApproachingCond;
  private ConditionVariable mNextOutputFileStartedCond;
  private boolean mExpectMaxFileCond;

  // movie length, in frames
  private static final int NUM_FRAMES = 120;

  private static final int TEST_R0 = 0;                   // RGB equivalent of {0,0,0} (BT.601)
  private static final int TEST_G0 = 136;
  private static final int TEST_B0 = 0;
  private static final int TEST_R1 = 236;
  // RGB equivalent of {120,160,200} (BT.601)
  private static final int TEST_G1 = 50;
  private static final int TEST_B1 = 186;

  private final static String AVC = MediaFormat.MIMETYPE_VIDEO_AVC;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cts_recoder_mpeg2_ts);
    new Thread(() -> {
      OUTPUT_PATH = new File(Environment.getExternalStorageDirectory(),
          "record.out").getAbsolutePath();
      OUTPUT_PATH2 = new File(Environment.getExternalStorageDirectory(),
          "record2.out").getAbsolutePath();
    }).start();
    mMediaRecorder = new MediaRecorder();
    findViewById(R.id.start).setOnClickListener((view) -> {
      try {
        mMediaRecorder.setOutputFile(OUTPUT_PATH);
        testRecorderMPEG2TS();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public void testRecorderMPEG2TS() throws Exception {
    int width;
    int height;
    Camera camera = null;
    //if (!hasCamera()) {
    //  MediaUtils.skipTest("no camera");
    //  return;
    //}
    //if (!hasMicrophone() || !hasAac()) {
    //  MediaUtils.skipTest("no audio codecs or microphone");
    //  return;
    //}
    // Try to get camera profile for QUALITY_LOW; if unavailable,
    // set the video size to default value.
    CamcorderProfile profile = CamcorderProfile.get(
        0 /* cameraId */, CamcorderProfile.QUALITY_LOW);
    if (profile != null) {
      width = profile.videoFrameWidth;
      height = profile.videoFrameHeight;
    } else {
      width = VIDEO_WIDTH;
      height = VIDEO_HEIGHT;
    }
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    mMediaRecorder.setVideoSize(width, height);
    mMediaRecorder.setVideoEncodingBitRate(VIDEO_BIT_RATE_IN_BPS);
    mMediaRecorder.setPreviewDisplay(null);
    mMediaRecorder.prepare();
    mMediaRecorder.start();
    Thread.sleep(RECORD_TIME_MS);

    // verify some getMetrics() behaviors while we're here.
    PersistableBundle metrics = mMediaRecorder.getMetrics();
    if (metrics == null) {
      Log.e(TAG, "MediaRecorder.getMetrics() returned null metrics");
    } else if (metrics.isEmpty()) {
      Log.e(TAG, "MediaRecorder.getMetrics() returned empty metrics");
    } else {
      int size = metrics.size();
      Set<String> keys = metrics.keySet();

      if (size == 0) {
        Log.e(TAG, "MediaRecorder.getMetrics().size() reports empty record");
      }

      if (keys == null) {
        Log.e(TAG, "MediaMetricsSet returned no keys");
      } else if (keys.size() != size) {
        Log.e(TAG, "MediaMetricsSet.keys().size() mismatch MediaMetricsSet.size()");
      }

      // ensure existence of some known fields
      int videoBitRate = metrics.getInt(MediaRecorder.MetricsConstants.VIDEO_BITRATE, -1);
      if (videoBitRate != VIDEO_BIT_RATE_IN_BPS) {
        Log.e(TAG, "getMetrics() videoEncodeBitrate set " +
            VIDEO_BIT_RATE_IN_BPS + " got " + videoBitRate);
      }

      // valid values are -1.0 and >= 0;
      // tolerate some floating point rounding variability
      double captureFrameRate = metrics.getDouble(MediaRecorder.MetricsConstants.CAPTURE_FPS, -2);
      if (captureFrameRate < 0.) {
        Log.e(TAG,
            "getMetrics() capture framerate=" + captureFrameRate + -1.0 + captureFrameRate + 0.001);
      }
    }

    mMediaRecorder.stop();
    //checkOutputExist();
  }
}