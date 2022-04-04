adb install CtsVideoTestCases.apk

adb shell am instrument -e class android.video.cts.VideoEncoderDecoderTest#testVp8Goog0Perf0320x0180 -w android.video.cts/androidx.test.runner.AndroidJUnitRunner

pause