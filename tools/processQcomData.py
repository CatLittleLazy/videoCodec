# coding:utf-8
#!/usr/bin/python3
import os
import sys
import time
import codecs
import linecache
import webbrowser
from datetime import datetime

file_name = ""
file_line_count = 0

QBUF_INPUT = {}
DQBUF_INPUT = {}
DQBUF_OUTPUT = {}

def getTimeToProcess(timeStr):
	time_array = datetime.strptime(str(timeStr), "%Y-%m-%d %H:%M:%S.%f")
	return time.mktime(time_array.timetuple()) * 1000.0 + time_array.microsecond / 1000

def diff(first, second):
	processTime = []
	timeStamp = []
	lostFrameLog = []
	moreThan40 = {}
	for k in first:
		if k in second:
			timeDiff = getTimeToProcess(str(second[k])) - getTimeToProcess(str(first[k]))
			print(timeDiff)
			timeStamp.append(k)
			processTime.append(timeDiff)
			if timeDiff > 40:
				moreThan40[k] = timeDiff
		else:
			lostFrameLog.append(k)
	lostCount = str(len(lostFrameLog))
	print("丢失" + lostCount +"无效数据, 对应时间戳如下:\n" + str(lostFrameLog))
	count = str(len(timeStamp))
	moreThan40count = str(len(moreThan40))
	print("\n\n共" + count +"个有效数据")
	print("\n\n解码超过40ms的数据有" + moreThan40count + "个, 解码时间如下:\n" + str(moreThan40))
	fileHtmlName = str(timeStamp[0]) + "~" + str(timeStamp[int(count) - 1]) + "(" + str(count) + ")" + ".data"
	fo = open(fileHtmlName, "w")
	fo.write(str(timeStamp) + "\n" + str(processTime))
	fo.close()
	print("文件路径： " + os.getcwd() + os.sep + fileHtmlName)



def processData():
	print("共抓取日志" + str(file_line_count) + "行\n\n")
	lineCount = 0
	while True:
		if lineCount > file_line_count:
			break
		info = linecache.getline(file_name, lineCount)
		lineCount = lineCount + 1
		if 'time-stamp 0' in info:
			continue
		if 'time-stamp' in info:
			data = info.split('time-stamp')
			currentTime = '2021-' + info.split( )[0] + ' ' + info.split( )[1]
			stamp = int(data[1])
			if ' QBUF: INPUT:' in info:
				QBUF_INPUT[stamp] = currentTime
			elif ' DQBUF: INPUT:' in info:
				DQBUF_INPUT[stamp] = currentTime
			elif ' DQBUF: OUTPUT:' in info:
				DQBUF_OUTPUT[stamp] = currentTime
	diff(QBUF_INPUT, DQBUF_OUTPUT)
	print(len(QBUF_INPUT))
	print(len(DQBUF_OUTPUT))
	print(len(DQBUF_INPUT))
	os.system("pause")


if __name__ == '__main__':
	file_name = sys.argv[1]
	file_line_count = len(linecache.getlines(file_name))
	processData()
