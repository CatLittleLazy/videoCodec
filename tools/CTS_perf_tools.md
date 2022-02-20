# one tools for android MediaPerf cts 

## 0、what、why、when、where、who、how

## 1、

## 6、how

1. use google cts creat the faileure results
2. write python to get the faliure test cases
3. auto run these failure test cases 10 and create zip
4. use google python create the correct frame rate
5. get the phone xml and replace
6. retry the failure teste cases





run cts-dev -m CtsMediaTestCases -t android.media.cts.VideoDecoderPerfTest#testVp8Goog0Perf0320x0180 --retry-strategy ITERATIONS --max-testcase-run-count 10

rm SF1346439884568

adb shell am instrument -e class android.media.cts.VideoDecoderPerfTest#testVp8Goog0Perf0320x0180 -w android.media.cts/androidx.test.runner.AndroidJUnitRunner

### 



ll 排序

https://www.cnblogs.com/pipiyan/p/10600058.html