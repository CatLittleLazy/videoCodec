import requests
import random
import os
import requests.adapters
import json
import time
import math
# import xlwings as xw

print("使用api获取理性人数据, 删除文件后每次使用消耗网络请求57(1 + 52 + 1 + 1 + 1 + 1)次")

token = "d4f4d920-033d-4c37-8757-f78180beb18e"

api = [
	"https://open.lixinger.com/api/cn/company", # 所有股票信息
	"https://open.lixinger.com/api/cn/company/fundamental/" # 获取股票详细信息
]

requestContent = [
	"d_pe_ttm_pos10", # PE-TTM(扣非)分位点(10年) 
	"pb_wo_gw_pos10", # PB(不含商誉)分位点(10年)
	"dyr" # 股息率
]

maxRequestDataCount = 100

def getTotalCompanyData():
	return requests.post(url = api[0], json = {"token":token})

def getCompanyDetailDataFromNetOnce(data, companyType, date):
	requestBody = {
			"token": token,
			"date": "2023-02-03",
			"stockCodes": data,
			"metricsList": [
				requestContent[0],
				requestContent[1],
				requestContent[2]
			]
		}
	return requests.post(url = (api[1] + companyType), json = requestBody).json()

def getFailedRequestCompanyStockCode(requestData, resultData):
	res = []
	failedData = []
	for tmp in resultData:
		res.append(tmp['stockCode'])
	for tmp in requestData:
		if tmp not in res:
			# print(tmp)
			failedData.append(tmp)
	return failedData

def getCompanyDetailDataFromNet(data, companyType, date):
	# print(data)
	dataSize = len(data)
	if dataSize <= maxRequestDataCount :
		print("请求数据中")
		return getCompanyDetailDataFromNetOnce(data, companyType, date)
	else:
		totalRequestCount = int(dataSize / maxRequestDataCount)
		if int(dataSize % maxRequestDataCount) != 0:
			totalRequestCount = totalRequestCount + 1
		print("共计" + str(dataSize) + "条数据, 需要请求" + str(totalRequestCount) + "次")
		result = {}
		result['code'] = 1
		result['message'] = 'success'
		resultData = []
		failedRequestData = []
		for count in range(0, totalRequestCount):
			requestData = data[count * maxRequestDataCount : (count + 1) * maxRequestDataCount]
			if count == totalRequestCount - 1:
				requestData = data[count * maxRequestDataCount : dataSize]
			print("第" + str(count + 1) + "次请求, 请求" + str(len(requestData)) + "条数据")	
			resultOnce = getCompanyDetailDataFromNetOnce(requestData, companyType, date)
			# print(resultOnce)
			if 1 == resultOnce['code']:
				resultData.extend(resultOnce['data'])
				resultOnceData = resultOnce['data']
				resultOnceDataSize = len(resultOnceData)
				if resultOnceDataSize != len(requestData):
					print("本次请求遗漏" + str(len(requestData) - resultOnceDataSize) + "条数据")
					failedRequestData.extend(getFailedRequestCompanyStockCode(requestData, resultOnceData))
			else:
				print("第" + str(count + 1) + "组数据请求失败")
				print(resultOnce['error']['messages'][0]['message'])
				failedRequestData.extend(requestData)
		if len(resultData) == dataSize:
			print("数据完整")
		else:
			# print("存在" + str(dataSize - len(resultData)) + "条数据遗漏, 尝试再次请求")
			print("存在" + str(len(failedRequestData)) + "条数据遗漏, 尝试再次请求")
			lastResultOnce = getCompanyDetailDataFromNetOnce(failedRequestData, companyType, date)
			lastFailedRequestData = []
			if 1 == lastResultOnce['code']:
				resultData.extend(lastResultOnce['data'])
				lastResultOnceData = lastResultOnce['data']
				lastResultOnceDataSize = len(lastResultOnceData)
				if lastResultOnceDataSize != len(failedRequestData):
					lastFailedRequestData.extend(getFailedRequestCompanyStockCode(failedRequestData, lastResultOnceData))
			else:
				print("第" + str(count + 1) + "组数据请求失败")
				print(resultOnce['error']['messages'][0]['message'])
				lastFailedRequestData.extend(requestData)
		print("经过最终尝试，依旧存在" + str(len(lastFailedRequestData)) + "条数据请求失败,请根据如下股票代码自行检查")
		for tmp in lastFailedRequestData:
			print(tmp)
		result['data'] = resultData
		# return json.loads(json.dumps(result, indent = 4))
		# print(type(result))
		return result

def getCompanyDetailData(data, companyType):
	companyDetail = ""
	todayDate = time.strftime("%Y-%m-%d",time.localtime())
	file_name = companyType + "_" + todayDate + ".txt"
	if os.path.exists(file_name) == True:
		print(companyType + "数据已存在")
		companyDetail = open(file_name,"r").read()
	else :
		result = getCompanyDetailDataFromNet(data, companyType, todayDate)
		# print(type(result))
		if 1 == result['code']:
			file = open(file_name,'w')
			# 向文件中输入字符串
			file.write(json.dumps(result))
			file.close()
			print("所有" + companyType + "类型股票数据已成功写入")
			companyDetail = result
		else:
			print("数据请求失败")
			print(result)
	return companyDetail

def mockData(data, flag):
	dataSize = len(data)
	totalRequestCount = int(dataSize / maxRequestDataCount) + 1
	for count in range(0, totalRequestCount):
		requestData = data[count * maxRequestDataCount : (count + 1) * maxRequestDataCount]
		if count == totalRequestCount:
			requestData = data[count * maxRequestDataCount : dataSize]
		print("第" + str(count) + "次请求, 请求" + str(len(requestData)) + "条数据")
		# print(requestData)
		if count == flag:
			return requestData

test = {
	"code": 1,
	"message": "success",
	"data": [
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": -261.01039554181176,
			"mc": 2315904000,
			"stockCode": "600421"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 29.29515445893459,
			"mc": 13079912015.25,
			"stockCode": "600422"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 259.52836044691765,
			"mc": 2899262944.38,
			"stockCode": "600423"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 12.643305460953842,
			"mc": 5611675650.02,
			"stockCode": "600425"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 10.467812729786786,
			"mc": 75123061564.62001,
			"stockCode": "600426"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 23.3511767009889,
			"mc": 15327086504.939999,
			"stockCode": "600428"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 53.12463315724489,
			"mc": 7489214400.179999,
			"stockCode": "600429"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 41.98160896335097,
			"mc": 8298392301,
			"stockCode": "600433"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 88.94437007367334,
			"mc": 18020772000,
			"stockCode": "600435"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 74.32631990584593,
			"mc": 183770422166,
			"stockCode": "600436"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 7.689115974120056,
			"mc": 184489751055.06,
			"stockCode": "600438"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 56.625169563557485,
			"mc": 3033720979.2000003,
			"stockCode": "600439"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 35.422368212337126,
			"mc": 2023551100.24,
			"stockCode": "600444"
		},
		{
			"date": "2023-02-03T00:00:00+08:00",
			"pe_ttm": 75.64166018661936,
			"mc": 12597512916.95,
			"stockCode": "600446"
		}
	]
}

# data 
# companyData = {
#   "code": 1,
#   "message": "success",
#   "data": [
#     {
#       "date": "2023-02-03T00:00:00+08:00",
#       "dyr": 0.0014297541391817613,
#       "d_pe_ttm_pos10": 0.1976950354609929,
#       "pb_wo_gw_pos10": 0.43882978723404253,
#       "stockCode": "300750"
#     },
#     {
#       "date": "2023-02-03T00:00:00+08:00",
#       "dyr": 0.02397414741474148,
#       "d_pe_ttm_pos10": 0.7437165224557066,
#       "pb_wo_gw_pos10": 0.8195302843016069,
#       "stockCode": "600519"
#     }
#   ]
# }

def analysisData(stockDatas, companyDatas):
	highQualityStock = []
	highQualityStockId = []
	for tmpStock in stockDatas:
		# PE-TTM(扣非)分位点(10年) 
		d_pe_ttm_pos10 = int(tmpStock[requestContent[0]] * 100000)
		# PB(不含商誉)分位点(10年)
		pb_wo_gw_pos10 = int(tmpStock[requestContent[1]] * 100000)
		# 股息率
		dyr = int(tmpStock[requestContent[2]] * 100000)
		d_pe_match = d_pe_ttm_pos10 - 20000 <= 0
		pb_wo_match = pb_wo_gw_pos10 - 20000 <= 0
		dyr_match = dyr >= 8000
		if d_pe_match and pb_wo_match and dyr_match:
			highQualityStock.append(tmpStock)
			highQualityStockId.append(tmpStock['stockCode'])
	print("共有" + str(len(stockDatas)) + "条数据, 其中优质股有"+ str(len(highQualityStock)) + "个")
	highQualityCompany = []
	for tmpCompany in json.loads(companyDatas)['data']:
		if len(highQualityCompany) == len(stockDatas):
			break
		if tmpCompany['stockCode'] in highQualityStockId:
			highQualityCompany.append(tmpCompany)
	# print(highQualityCompany)
	companyStock = []
	for tmpCompany in highQualityCompany:
		for tmpStock in highQualityStock:
			tmpCompanyStock = {}
			if tmpCompany['stockCode'] == tmpStock['stockCode']:
				# tmpCompany[requestContent[0]] = "%.2f"%(tmpStock[requestContent[0]]*100)+"%"
				# tmpCompany[requestContent[1]] = "%.2f"%(tmpStock[requestContent[1]]*100)+"%"
				# tmpCompany[requestContent[2]] = "%.2f"%(tmpStock[requestContent[2]]*100)+"%"
				tmpCompanyStock["公司名称"] = tmpCompany['name']
				tmpCompanyStock["股票代码"] = tmpCompany['stockCode']
				tmpCompanyStock["ipo日期"] = tmpCompany['ipoDate'].split('T')[0]
				tmpCompanyStock["PE-TTM(扣非)分位点(10年)"] = "%.2f"%(tmpStock[requestContent[2]]*100)+"%"
				tmpCompanyStock["PB(不含商誉)分位点(10年)"] = "%.2f"%(tmpStock[requestContent[1]]*100)+"%"
				tmpCompanyStock["股息率"] = "%.2f"%(tmpStock[requestContent[2]]*100)+"%"
				companyStock.append(tmpCompanyStock)
	for company in companyStock:
		print(company)
	# saveToExcel(companyStock)
	print(companyStock[0].keys())

def addData(data):
	if type(data) == str:
		return json.loads(data)['data']
	else:
		return data['data']

def saveToExcel(sotckDatas):
	wb = app.books.add()
	sht = wb.sheets["sheet1"]
    # for tmpKey in stockDatas.keys():
	sht.range("A1").value = "commitId缩写" 
	sht.range("B1").value = "commitId" 
	sht.range("C1").value = "日期" 
	sht.range("D1").value = "changeId" 
	sht.range("E1").value = "说明" 
	print("共有" + str(len(commitDatas)) + "条数据，正在努力写入中，请耐心等候...")
	for index in range(len(commitDatas)):
		tmpCommit = commitDatas[index]
		sht.range("A"+str(index+2)).value = str(tmpCommit['commitId'])[:7]
	# 保存文件
	wb.save('./test.xlsx')
	wb.close()
	app.quit()

if __name__ == '__main__':
	todayDate = time.strftime("%Y-%m-%d",time.localtime())
	totalCompanyInfo = ""
	if os.path.exists("totalCompanyInfo.txt") == True:
		print("所有股票数据已存在")
		totalCompanyInfo = open("totalCompanyInfo.txt","r").read()
	else :
		data = getTotalCompanyData()
		if "success" in data.text:
			file = open('totalCompanyInfo.txt','w')
			# 向文件中输入字符串
			file.write(data.text)
			file.close()
			print("所有股票数据已成功写入")
			totalCompanyInfo = data.text
		else:
			print("数据请求失败")
			print(data.text)
	totalCompanyInfoData = json.loads(totalCompanyInfo)['data']
	print("共有" + str(len(totalCompanyInfoData)) + "个股票")
	stockCode = [];
	non_financial = []
	bank = []
	insurance = []
	security = []
	other_financial = []
	for tmp in totalCompanyInfoData:
		# print(tmp['fsType'])
		if tmp['fsType'] == "non_financial":
			non_financial.append(tmp['stockCode'])
		if tmp['fsType'] == "bank":
			bank.append(tmp['stockCode'])
		if tmp['fsType'] == "insurance":
			insurance.append(tmp['stockCode'])
		if tmp['fsType'] == "security":
			security.append(tmp['stockCode'])
		if tmp['fsType'] == "other_financial":
			# print(tmp)
			other_financial.append(tmp['stockCode'])
		# try:
		# 	print(tmp['ipoDate'])
		# except KeyError as error:
		# 	print("test")
	print("非金融: " + str(len(non_financial)))
	non_financial_data = getCompanyDetailData(non_financial, "non_financial")	
	# 模拟获取请求遗漏数据
	# req = mockData(non_financial, 4)
	# getFailedRequestCompanyStockCode(req, test)
	print("银行: " + str(len(bank)))
	bank_data = getCompanyDetailData(bank, "bank")
	print("保险: " + str(len(insurance)))
	insurance_data = getCompanyDetailData(insurance, "insurance")
	print("证券: " + str(len(security)))
	security_data = getCompanyDetailData(security, "security")
	print("其他金融: " + str(len(other_financial)))
	other_financial_data = getCompanyDetailData(other_financial, "other_financial")
	# print(other_financial_data)
	totalStockDatas = []
	totalStockDatas.extend(addData(non_financial_data))
	totalStockDatas.extend(addData(bank_data))
	totalStockDatas.extend(addData(insurance_data))
	totalStockDatas.extend(addData(security_data))
	totalStockDatas.extend(addData(other_financial_data))
	analysisData(totalStockDatas, totalCompanyInfo)

