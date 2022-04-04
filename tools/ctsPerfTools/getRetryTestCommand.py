# coding:utf-8
# !/usr/bin/python3
import os
import linecache
try:
	import xml.etree.cElementTree as ET
except ImportError:
	import xml.etree. ElementTree as ET
from xml.etree import ElementTree
import zipfile

def getAdbTestCommand(testCaseName,testName):
    packageName = '.'.join(testCaseName.split(".")[0:3])
    testCommand = f'''adb shell am instrument -e class {testCaseName}#{testName} -w {packageName}/androidx.test.runner.AndroidJUnitRunner'''
    # print(testCommand)
    return testCommand

def getRetryCommand(xmlContent):
	testResultXml = ElementTree.fromstring(xmlContent)
	allModule = testResultXml.findall("Module")
	# pirnt 
	ctsRetryCommand = ""
	adbRetryCommand = []
	adbRetryCommand.append("adb install CtsMediaTestCases")
	adbRetryCommand.append("adb shell pm grant android.media.cts android.permission.MANAGE_EXTERNAL_STORAGE")
	adbRetryCommand.append("adb install CtsVideoTestCases")
	adbRetryCommand.append("adb shell pm grant android.video.cts android.permission.MANAGE_EXTERNAL_STORAGE")
	testFailureName = []
	for module in allModule:
		moduleName = module.get("name")
		# print(moduleName)
		adbTestCommand = ""
		for testCase in module:
			testCaseName = testCase.get("name")
			# print(testCaseName)
			for test in testCase:
				testResult = test.get("result")
				testName = test.get("name")
				if "fail" == testResult and testName not in testFailureName:
					testFailureName.append(testName)
					# print(testName)
					adbRetryCommand.append(getAdbTestCommand(testCaseName,testName))
					ctsRetryCommand =  ctsRetryCommand + " --include-filter \"" + moduleName + " " + testCaseName + "#" + testName + "\""
					# print(ctsRetryCommand)
	retryCount = input("请输入复测次数(仅对套件环境生效):\n")
	ctsRetryCommand = "run cts" + ctsRetryCommand + " --retry-strategy ITERATIONS --max-testcase-run-count " + retryCount
	print("Cts 套件测试环境如下：\n")
	print(ctsRetryCommand + "\n")
	# print(adbRetryCommand)
	isUbuntun = input("请输入电脑系统(用于生成脚本文件)：1.windows;2.linux\n")
	if isUbuntun == "2":
		fo = open("adb_retry.sh", "w")
		print("adb 复测命令已生成：adb_retry.sh")
	else :
		adbRetryCommand.append("pause")
		fo = open("adb_retry.bat", "w")
		print("adb 复测命令已生成：adb_retry.bat")
	fo.write(str(adbRetryCommand).replace('[','').replace(']','').replace(',','\n').replace('\'',''))
	fo.close
	if isUbuntun == "2":
		os.system("chmod a+x adb_retry.sh")

def parse_result(result):
	if not result.endswith('.zip'):
		print(sys.stderr, "cannot parse %s" % result)
		return
	try:
		with zipfile.ZipFile(result) as zip:
			for info in zip.infolist():
				# print(info.filename)
				if "test_result.xml" in info.filename:
					xmlFile = zip.read(info)
					# print(xmlFile)
					getRetryCommand(xmlFile)
	except zipfile.BadZipfile:
		raise ValueError('bad zipfile')

if __name__ == '__main__':
	resultZipNames = input("请输入帧率测试失败报告文件:\n")
	parse_result(resultZipNames)


