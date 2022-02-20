import os
import sys
from pathlib import Path

# use like this
# python3 libHelper.py out/target/product/blueline/system/lib/libmediaplayerservice.so,out/target/product/blueline/system/lib/libmediaplayerservice.so,a_patch,sh

bat_content = []
bat_content.append("adb root")
bat_content.append("adb remount")

reback_content = []
reback_content.append("adb root")
reback_content.append("adb remount")

command = sys.argv[1].split(',')
file_extension = command[len(command) - 1]
dstDirPath = command[len(command) - 2]
print("your patch dst dir is = " + dstDirPath)

my_file_directory = Path(dstDirPath)

if my_file_directory.is_dir() == False:
	os.system('mkdir ' + dstDirPath)

command.remove(dstDirPath)
command.remove(file_extension)
dstPath = []
for soPath in command:
	if soPath in dstPath:
		tmpDirs = soPath.split(os.sep)
		count = len(tmpDirs)
		replaceStr = tmpDirs[count - 2]
		if replaceStr == "lib":
			shouldShowStr = "lib64" + os.sep
		else:
			shouldShowStr = "lib" + os.sep
		soPath = soPath.replace(replaceStr + os.sep, shouldShowStr)
	dstPath.append(soPath)

for tmp in dstPath:
	cmd = "cp --path " + tmp + " " + dstDirPath
	tmpDirs = tmp.split(os.sep)
	count = len(tmpDirs)
	adb_push_dir = tmpDirs[count - 3] + os.sep + tmpDirs[count - 2] + os.sep
	adb_push_command = "adb push " + tmp + " " + adb_push_dir
	mk_bak_command = "mkdir -p bak" + os.sep + adb_push_dir
	adb_pull_command = "adb pull " + adb_push_dir + tmpDirs[count - 1] + " bak" + os.sep + adb_push_dir
	adb_reback_command = "adb push " + "bak" + os.sep + adb_push_dir + tmpDirs[count - 1] + " " + adb_push_dir
	print(adb_push_command)
	bat_content.append(mk_bak_command)
	bat_content.append(adb_pull_command)
	bat_content.append(adb_push_command)
	reback_content.append(adb_reback_command)
	os.system(cmd)

my_bat_file = Path(dstDirPath + os.sep + "replace." + file_extension)
if my_bat_file.is_file():
	os.system("rm -rf " + str(my_bat_file))
	# print("the relpace file is already exists")

bat_content.append("pause")
bat_content.append("adb reboot")

fo = open(my_bat_file, "w")
fo.write(str(bat_content).replace('[','').replace(']','').replace(',','\n').replace('\'',''))
fo.close

reback_bat_file = Path(dstDirPath + os.sep + "reback." + file_extension)
if reback_bat_file.is_file():
	os.system("rm -rf " + str(reback_bat_file))
	# print("the reback file is already exists")

reback_content.append("pause")
reback_content.append("adb reboot")
fo = open(reback_bat_file,"w")
fo.write(str(reback_content).replace('[','').replace(']','').replace(',','\n').replace('\'',''))
fo.close()
