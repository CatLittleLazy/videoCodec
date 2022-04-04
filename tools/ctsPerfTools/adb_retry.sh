adb install CtsMediaTestCases.apk
 adb shell pm grant android.media.cts android.permission.MANAGE_EXTERNAL_STORAGE
 adb install CtsVideoTestCases.apk
 adb shell pm grant android.video.cts android.permission.MANAGE_EXTERNAL_STORAGE
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testAvcOther0Perf0720x0480 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testH263Goog0Perf0176x0144 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testH263Goog0Perf0352x0288 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testHevcOther0Perf3840x2160 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testMpeg4Goog0Perf0176x0144 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testVp8Goog0Perf0320x0180 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
 adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testVp9Goog0Perf0320x0180 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner
