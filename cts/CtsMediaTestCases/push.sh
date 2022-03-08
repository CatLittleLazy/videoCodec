adb root

adb shell mkdir -p sdcard/dynamic-config-files/

adb push CtsMediaTestCases.dynamic sdcard/dynamic-config-files/

adb install CtsMediaTestCases.apk

adb shell pm grant android.media.cts android.permission.CAMERA

adb shell pm grant android.media.cts android.permission.RECORD_AUDIO

adb shell pm grant android.media.cts android.permission.WRITE_EXTERNAL_STORAGE


adb install ctsContentProvider.apk

adb shell pm grant android.tradefed.contentprovider android.permission.WRITE_EXTERNAL_STORAGE

adb shell am instrument -e class android.media.cts.MediaCodecCapabilitiesTest#testAvcBaseline12 -w -r android.media.cts/androidx.test.runner.AndroidJUnitRunner

pause
