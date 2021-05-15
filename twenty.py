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
import tkinter as tk
import threading

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
				# 关闭倒计时label
				l1_2.destroy()
				# 关闭倒计时window
				window1.destroy()
				return
			# 设置倒计时时间(需要将小数转换为整数)
			l1_2.configure(text = int(create_now)-int(seconds_now))
			# 设置倒计时文字位置
			l1_2.place(x = 0, y = 0)
			# 每1000毫秒后调用一次自身
			l1_2.after(1000, time_now)

		window1 = tk.Tk()
		window1.title('20 20 20')
		window1.overrideredirect(True)
		width = window1.winfo_screenwidth()+100
		height = window1.winfo_screenheight()+100
		# 设置窗口大小为200*25，位置为屏幕左下角
		resolution = str(width)+'x'+str(height)+'+0+'+str(width-25)
		print(resolution)
		window1.geometry('40x25+0+1055')
		# 窗口置顶显示
		window1.wm_attributes('-topmost',1)
		# 创建倒计时label
		l1_2 = tk.Label(window1, text = 1200, font = ('宋体', 11))
		# 获取创建时间，在其基础上增加1200即为20分钟后的时间
		global create_now
		create_now = time.time() + 1200
		# 开启倒计时
		time_now()
		window1.mainloop()
	else:
		def time_now():
			global seconds_now
			seconds_now = time.time()
			if seconds_now > create_now:
				window2.destroy()
				return
			window2.after(1000, time_now)

		window2 = tk.Tk()
		window2.title('20 20 20')
		window2.overrideredirect(True)
		width = window2.winfo_screenwidth()+100
		height = window2.winfo_screenheight()+100
		resolution = str(width)+'x'+str(height)
		window2.geometry(resolution)
		window2['bg'] = 'black'
		window2.wm_attributes('-topmost',1)
		create_now = time.time() + 50
		time_now()
		print('--------->')
		window2.mainloop()

if __name__ == '__main__':
	while True:
		main(1)
		main(2)
