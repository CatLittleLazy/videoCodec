# coding:utf-8
# !/usr/bin/python3
import os
import linecache
import get_achievable_rates
try:
	import xml.etree.cElementTree as ET
except ImportError:
	import xml.etree. ElementTree as ET
from xml.etree import ElementTree
from get_achievable_rates import justTry

#只是为了xmI内容中注释部分不丢失
class CommentedTreeBuilder(ElementTree. TreeBuilder):
	def comment(self,data):
		#self.start(ElementTree.Comment,{})
		#兼容问题，这里使用lll代表<,rrr代表>, data添加
		self.data("lll!--" + data.replace("<","lll").replace(">","rrr") + "--rrr")
		#self.end(ElementTree.Comment)

#无用方法(解析xml练手)
def get(oot,codecName):
	#遍历xml文档的第二层
	for child in root: 
		#第二层节点的标签名称和属性
		print(child.tag,":",child,attrib)
		#遍历xml文档的第三层
		for children in child:
			#第三层节点的标签名称和属性
			print(children.tag,children.attrib)
			tmpName = children.get("name")
			for limit in children:
				print(imit.tag,limit.attrib)

#处理xml内容的缩进问题
def indent(elem, level=0):
	i = "\n" + level * "    "
	if len(elem):
		if not elem.text or not elem.text.strip():
			elem.text= i + "    "
		if not elem.tail or not elem.tail.strip():
			elem.tail= i
		for elem in elem:
			indent(elem, level+1)
		if not elem.tail or not elem.tail.strip():
			elem.tail= i
	else:
		if level and (not elem.tail or not elem.tail.strip()):
			elem.tail= i
	return elem

#自动修复方法，old为手机目前使用的xml,new为执行谷歌脚本后自动生成的xml
def tryFixed(old,new):
	#可能既有encoder、又有decoder需要修改
	for index in range(0,len(new)):
		#获取当前是Encoders还是Decoders
		codeType = new[index].tag
		#print(codeType)
		#找出所有MediaCodec节点后进行遍历
		for fCodecNode in new[index].findall("MediaCodec"):
			#获取当前codecName,eg:c2. android.h263.decoder
			fCodecName = fCodecNode.get("name")
			#获取当前codec存在的Limit列表
			fLimits = fCodecNode.findall("Limit")
			#print(fCodecName)
			print("--------")
			#在手机使用的xml中找到对应的Decoder或Encoder后遍历MediaCodec
			for pCodecNode in old.find(codeType).findall("MediaCodec"):
				#获取当前codecName,eg:c2.android.h263.decoder
				pCodecName = pCodecNode.get("name")
				#获取当前codec存在的Limit列表
				pLimits = pCodecNode.findall("Limit")
				#当codec名称相同时
				if fCodecName == pCodecName:
					print(fCodecName)
					#继续匹配imits
					for fLimit in fLimits:
						hasChanged = True
						fLimitName = fLimit.get("name")
						for pLimit in pLimits:
							pLimitName = pLimit.get("name")
							#当limit名称相同时视为找到修改点,eg:measured-frame-rate-1280x720
							if fLimitName == pLimitName:
								#打印修改点
								print("\t"+ fLimitName + "range :" + pLimit.get("range") + "-->" + fLimit.get("range"))
								#对内容进行修改
								pLimit.set("range",fLimit.get("range"))
								hasChanged = False
								break
						#此处代码的逻辑为添加不存在的分辨节点
						if hasChanged:
							#默认添加下标为0
							index = 0
							#获取目前未添加节点总像素点
							fLimitPixel = getTotalPixel(fLimitName)
							for pLimit in pLimits:
								#比较像素点判断添加位置(存在问题若原手机文件中的分辨率乱序可能出现插入顺序错误,不美观但是不会影响测试结果)
								if getTotalPixel(pLimit.get("name")) < fLimitPixel:
									index = index + 1
							#添加至对应节点
							pCodecNode.insert(index ,fLimit)
							#打印添加节点信息
							print("you have append this node\n\t" + fLimitName + " range:" + fLimit.get("range"))
					break
	#添加/修改内容完成后进行缩进对齐
	indent(old)
	#替换I及rrr为<>后返回字符串
	return ET.tostring(old).decode().replace("lll","<").replace("rrr",">")

def getTotalPixel(limitName):
	resolution = limitName.replace("measured-frame-rate-","").split('x')
	width = int(resolution[0])
	height = int(resolution[1])
	return width * height

def getTopComment(fileName):
	lineTotal = len(linecache.getlines(fileName))
	topComment = ""
	for line in range(0,lineTotal):
		data = linecache.getline(fileName,line)
		#print(data.encode())
		if data != "<MediaCodecs>\n":
			topComment = topComment + data
		else :
			return topComment

def getPhoneCodecsPerformanceXml():
	variant = os.popen("adb shell getprop ro.media.xml_variant.codecs_performance").readlines()[0].replace("\n","")
	xmlName = "media_codecs_performance" + variant + "_old.xml" 
	command = "adb pull vendor/etc/" + xmlName.replace("_old","") + " " + xmlName
	# print(command)
	os.system(command)
	return xmlName


if __name__ == '__main__':
	# 1、获取手机media_codecs_performance.xml文件(高通或刷入gsi手机名称可能有后缀)
	phoneXmlName = getPhoneCodecsPerformanceXml()
	# 2、获取测试失败结果
	resultZipNames = input("请输入帧率测试失败报告文件(多个文件可用空格间隔):\n")
	# 3、调用谷歌脚本生成xml文件并获取名称
	fromGoogleXmlName = justTry(resultZipNames.split(' '))
	#print(os.system("python get_achievable_rates.py --ignore " + resultZipNames))
	# 4、获取手机xml文件头部注释内容
	topComment = getTopComment(phoneXmlName)
	# 5、构造可保存注释的解析器
	parser = ElementTree.XMLParser(target=CommentedTreeBuilder())
	# 6、解析手机中XML文件
	phoneXml = ElementTree.parse(phoneXmlName, parser=parser)
	# 7、解析谷歌生成XML文件
	fromGoogleXml = ET.parse(fromGoogleXmlName).getroot()
	# 8、调用比对方法方法获取修改完成后内容
	content = tryFixed(phoneXml.getroot(),fromGoogleXml)
	# 9、由于xml的write无法保存注释，我们自行拼接内容后生成文件
	finalXml = open(phoneXmlName.replace("_old",""),"w")
	finalXml.write(topComment + content)
	# 10、文件说明
	print("----------------------------------------------------")
	print("| 手机原始xml\n" + "|\t" + phoneXmlName)
	print("| 谷歌生成xml\n" + "|\t" + fromGoogleXmlName)
	print("| 对比生成xml\n" + "|\t" + finalXml.name)
	print("----------------------------------------------------")
	finalXml.close()

