# coding:utf-8
#!/usr/bin/python3
import linecache
import urllib.request
# 导入正则库
import re
import json
import xlwings as xw

# 姓氏，保存文件名，搜索有误姓氏记录
def getInfo(surnName,fileName,needSearchFileName):
    # 根据姓氏请求数据
    page = urllib.request.urlopen('https://baike.baidu.com/item/' + urllib.parse.quote(surnName+'姓', safe='/', encoding=None, errors=None))
    # 获取请求结果
    data = page.read()
    # 正则匹配出所需数据
    re_content = re.findall('(?<=content=").+?(?=")', data.decode('utf-8'))
    # 无数据时记录至文件
    if len(re_content) == 0:
        with open(needSearchFileName+'.txt', 'a',encoding='utf-8') as f:
            f.write('该姓氏搜索无结果:'+surnName+'\n')
            f.close()
            return 0
    # 搜索出的结果进行拼接json数据后写入文件
    elif re_content[2].find('。',0,len(re_content[2])-1) != -1:
        content = ''
        with open(fileName+'.txt', 'a',encoding='utf-8') as f:
            # 只提取前两句描述
            if len(re_content[2].split('。'))>2:
                content = re_content[2].split('。')[0] + '。' + re_content[2].split('。')[1]+'。'
            else:
                content = re_content[2]
            f.write(',{\"surnName\"'+':\"'+surnName +'\",\"Summary\":\"'+ content + '\"}')
            f.close()
            return 1
    # 多义姓氏需要单独搜索
    else:
        with open(needSearchFileName+'.txt', 'a',encoding='utf-8') as f:
            f.write('需要单独搜索的姓氏:'+surnName+'\n')
            f.close()
            return 0

if __name__ == "__main__":
    app = xw.App(visible=False, add_book=False)
    # 添加一个新的工作薄
    wb = app.books.add()
    sht = wb.sheets["sheet1"]
    sht.range("A1").value = "commitId缩写" 
    sht.range("B1").value = "commitId" 
    sht.range("C1").value = "日期" 
    sht.range("D1").value = "changeId" 
    sht.range("E1").value = "说明" 
    with open('frameworkBaseMediaLog.txt', encoding='utf-8') as commitHistory:
        data = str(commitHistory.read())
        gitLog = json.loads(data)
        # print(gitLog)
    commitDatas = gitLog['commitLogEntries']
    print("共有" + str(len(commitDatas)) + "条数据，正在努力写入中，请耐心等候...")
    for index in range(len(commitDatas)):
        tmpCommit = commitDatas[index]
        sht.range("A"+str(index+2)).value = str(tmpCommit['commitId'])[:7]
        sht.range("B"+str(index+2)).value = tmpCommit['commitId']
        sht.range("C"+str(index+2)).value = tmpCommit['commitTime']
        try:
            changeId = tmpCommit['metadata']['Change-Id']
        except:
            changeId = ""
        sht.range("D"+str(index+2)).value = changeId
        sht.range("E"+str(index+2)).value = tmpCommit['commitSubject']

    # 保存文件
    wb.save('./test.xlsx')
    wb.close()
    app.quit()