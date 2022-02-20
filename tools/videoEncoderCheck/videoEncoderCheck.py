# coding:utf-8
#!/usr/bin/python3
import os
import cv2
import sys
import time
import codecs
import linecache
import webbrowser
from datetime import datetime

# 文件名称
fileName = sys.argv[1]

# 视频fps
videoFps = 0

# 视频总帧数
videoFrames = 0

# 视频宽度
videoWidth = 0

# 视频高度
videoHeight = 0

# 理论每帧间隔  1000 / fps
videoFrameInterval = 0

encoderStamp = {}

def printEmptyLine():
    print("\n")

def getTheBasicVideoInfo():
    global videoFps
    global videoFrames
    global videoWidth
    global videoHeight
    global videoFrameInterval
    cap = cv2.VideoCapture(fileName)
    # 获取FPS(每秒传输帧数(Frames Per Second))
    videoFps = cap.get(cv2.CAP_PROP_FPS)
    print("视频帧率 = " + str(videoFps))
    printEmptyLine()
    # 计算理论每帧间隔
    videoFrameInterval = 1000 / videoFps
    print("每帧间隔 = " + str(videoFrameInterval))
    printEmptyLine()
    # 获取总帧数
    videoFrames = cap.get(cv2.CAP_PROP_FRAME_COUNT)
    print("视频帧数 = " + str(videoFrames))
    printEmptyLine()
    # 视频帧宽度
    videoWidth = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    print("视频宽度 = " + str(videoWidth))
    printEmptyLine()
    # 视频帧高度
    videoHeight = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    print("视频高度 = " + str(videoHeight))
    printEmptyLine()
    cap.release()


def processVideoData():
    global encoderStamp
    COUNT = 0
    cap = cv2.VideoCapture(fileName)
    cv2.namedWindow("video",0);
    # 后续可能还要进行比例缩放
    cv2.resizeWindow("video", videoWidth, videoHeight);
    # 若小于总帧数则读一帧图像
    while COUNT < 50:
        # 一帧一帧图像读取
        ret, frame = cap.read()
        # 把每一帧图像保存成jpg格式（这一行可以根据需要选择保留）
        cv2.imwrite(str(COUNT) + '.jpg', frame)
        # 显示这一帧地图像
        cv2.imshow('video', frame)
        COUNT = COUNT + 1
        # 这里对时间戳有取整操作
        mediaStamp = int(cap.get(cv2.CAP_PROP_POS_MSEC) * 1000)
        # print(mediaStamp)
        # 这里对最后的Eos做不记录处理
        if COUNT !=0 and mediaStamp == 0:
            continue
        encoderStamp[COUNT] = mediaStamp
        # 延时（1000 / fps）再读取下一帧，如果没有这一句便无法正常显示视频
        cv2.waitKey(int(videoFrameInterval))
    constructData()
    cap.release()

def constructData():
    timeStamp = list(encoderStamp.values())
    moreThanFrameInterval = {}
    for i in range(0,len(timeStamp)):
        if i == len(timeStamp) - 1:
            break
        timeDiff = timeStamp[i + 1] - timeStamp[i]
        # print(timeDiff)
        if timeDiff > videoFrameInterval * 1000 + 100:
            print("第" + str(i + 1) + "帧与下1帧间隔为: " + str(timeDiff) + ", 对应视频时间戳为: " + str(timeStamp[i]))
            moreThanFrameInterval[i] = str(timeDiff) + "_" + str(timeStamp[i])
    count = len(moreThanFrameInterval)
    if count != 0:
        print("超过理论每帧间隔数据共" + str(len(moreThanFrameInterval)) + "个, 数据如下")
        printEmptyLine()
        print(str(moreThanFrameInterval))
    else:
        print("视频源正常")

# todo next for just create data once
#def createSavePath():
    #my_bat_file = Path(dstDirPath + os.sep + "replace." + file_extension)
    #if my_bat_file.is_file():
    #    os.system("rm -rf " + str(my_bat_file))
        # print("the relpace file is already exists")


if __name__ == '__main__':
    print("\n文件名称 = " + fileName)
    printEmptyLine()
    # 获取视频基本参数
    #getTheBasicVideoInfo()
    # 处理并生成数据
    #processVideoData()
