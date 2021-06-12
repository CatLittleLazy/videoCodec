#!/usr/bin/python
# -*- coding: UTF-8 -*-
# using in ubuntu 20.04.2 LTS
# 1.add coding UTF-8
# 2.sudo apt-get install python3-tk (install tk in py3)
# 3.use python3 twenty.py to start
# 需求 20 20 20
# 20分钟20米远外的地方看20s

# 实现思路
# 运行后启动线程进行倒计时1200s，(20分钟)1200s后
# 展示一个全黑屏界面，倒计时20s后自动关闭屏幕
# 开始下一轮循环

## 还有5秒的时候需要弹出倒计时的对话框优化体验

# 导入需要的包
import time
# 如果提示tkinter不存在，请确定python目录下存在tcl目录,
import tkinter as tk
import threading

# 设置黑屏时间50s
REST_TIME = 50

# 设置工作时间1200s
WORK_TIME = 20 * 60

# 主屏1920x1080
SCRN_X = 1920
SCRN_Y = 1080

# 副屏
SCRN_2_X = 1024
SCRN_2_Y = 768

# 高度
LINE_HEIGHT = 1

# 时间线颜色(rgb)
LINE_COLOR = "#a9a9a9"

# 设置每秒钟宽度变化
PIXEL_PER_SEC = SCRN_X / WORK_TIME

#退出程序
def close(event):
    print('----close---')
    exit()

# 根据剩余时间计算时间线位置
def time_line_show_position(time_dec):
			newWidth = int(PIXEL_PER_SEC * time_dec)
			# time_dec == 0; return 1920 x 1 + 0 + 1079
			return str(newWidth)+'x'+str(LINE_HEIGHT)+'+'+str(SCRN_X - newWidth)+'+'+str(SCRN_Y - LINE_HEIGHT)

# flag: 1:倒计时界面 非1:黑屏界面
def main(flag):
	if flag == 1:
		# 定义当前时间函数
		def time_now():
			# 获取当前时间
			global seconds_now
			seconds_now = time.time()
			# 是否已经到了1200秒(20分钟)
			if seconds_now > create_now:
				# 关闭倒计时window
				window1.destroy()
				return
			window1.geometry(time_line_show_position(int(create_now) - int(seconds_now)))
			window1.wm_attributes('-topmost',1)
			# 每100毫秒后调用一次自身
			window1.after(100, time_now)

		window1 = tk.Tk()
		window1.title('20 20 20')
		window1.overrideredirect(True)
		# 以如下内容确定显示位置
		window1.geometry(time_line_show_position(0))
		# 窗口置顶显示
		window1.wm_attributes('-topmost',1)
		# 设置显示颜色
		window1['bg'] = LINE_COLOR
		# 获取创建时间，在其基础上增加1200即为20分钟后的时间
		global create_now
		create_now = time.time() + WORK_TIME
		#击后自动退出
		window1.bind("<Button-1>",close)
		time_now()
		window1.mainloop()
	else:
		def time_now():
			global seconds_now
			seconds_now = time.time()
			if seconds_now > create_now:
				window2.destroy()
				return
			window2.after(100, time_now)
		window2 = tk.Tk()
		window2.title('20 20 20')
		window2.bind("<Button-1>",close)
		window2.overrideredirect(True)
		resolution = str(SCRN_X + SCRN_2_X)+'x'+str(SCRN_Y + SCRN_2_Y)
		window2.geometry(resolution)
		window2['bg'] = 'black'
		window2.wm_attributes('-topmost',1)
		#设置黑屏时间50s
		create_now = time.time() + REST_TIME
		time_now()
		print('--------->')
		window2.mainloop()

if __name__ == '__main__':
	while True:
		main(1)
		main(2)

	
