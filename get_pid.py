import os
from time import sleep
import random

package_name = "providers.media"

# adb 模拟滑动手势(以步长200 从坐标350,1580 滑动到350,680)
def adb_swipe(n,time_count):
	x1 = round(350+random.uniform(-5,5),2)
	y1 = round(1580+random.uniform(-6,6),2) 
	x2 = round(350+random.uniform(-8,8),2)
	y2 = round(900+random.uniform(-7,7),2)
	step_ = round(200+random.randint(-9,9),2)
	time_sleep = random.uniform(6,8)
	os.system("adb shell input swipe {} {} {} {} {}".format(x1,y1,x2,y2,step_))
	print('正在看第{}个视频！看{}秒！总共观看{}分钟'.format(n,time_sleep,round(time_count/60,2)))
	sleep(time_sleep) 
	return time_sleep

def try_to_auto():
	n = 1
	time_count = 1
	while 1:
		time_sleep = adb_swipe(n,time_count)
		time_count += time_sleep
		n += 1

# 带返回值的命令
def adb_shell_with_result(op):
	#os.popen的返回结果是在一个文件中，需要读取，而且读取结果是列表
	return os.popen("adb shell " + op).readlines()

# 不关注shell命令返回值
def adb_shell_without_result(op,fileName):
	#os.system的返回结果是在shell命令的返回值，1或0
	os.system("adb shell " + op)


if __name__ == "__main__":
	# 使用\"拼接，防止默认commandLine报错grep未知
	result = adb_shell_with_result("\"ps -ef | grep " + package_name + " | grep -v grep\"")
	if len(result) != 0:
		pid = result[0].split( )[1]
		print(pid)
	else :
		print("...未找到相关进程,请确定包名正确...")
	os.system("pause")