# Polar应用

----

## 0. 背景

1. 目前使用的是h10，虽然没怎么用过，一年也就不到10次，最近想在手表上用，准备接下sdk在手表上显示下实时心率
2. 不知道啥时候开始计算的消耗也不准了，这也是一部分原因

## 1. 周边信息

1. polar sensor[设备对比](https://zhuanlan.zhihu.com/p/354612618)(2023.07.16)

   ![image-20230716173029511](polar应用.assets/image-20230716173029511.png)

2. polar部分的官网[sdk](https://github.com/polarofficial/polar-ble-sdk)

   ![image-20230716173108082](polar应用.assets/image-20230716173108082.png)

## 2. 开发

1. 按照官方demo添加依赖后运行报错参考该[提问解决](https://github.com/polarofficial/polar-ble-sdk/issues/136)，目前已经可以初始化api，不过飘红跟无提示问题赞为解决，可能与sdk是kt编写的有关

   ![image-20230716212441766](polar应用.assets/image-20230716212441766.png)

   ![image-20230716212747943](polar应用.assets/image-20230716212747943.png)

2. 好了，一顿折腾，终于是给连接上出数据了

   ![image-20230716223255786](polar应用.assets/image-20230716223255786.png)

3. 正好H10支持双设备，可以测试一下他家应用跟日志数据正好，数据跟预期一样正常

   ![image-20230716224219223](polar应用.assets/image-20230716224219223.png)

4. 接下来接着梳理下逻辑，搞个桌面小组件或者在手表上做个demo，还是得想办法解决掉不提示的问题，要不真的太麻烦了

   1. 重新下载安装android [studio](https://developer.android.com/studio). 

      ![image-20230819224457425](polar应用.assets/image-20230819224457425.png)

   2. 升级AGP至7.4.2

      ![image-20230819224532997](polar应用.assets/image-20230819224532997.png)

   3. 在task中编译出polar的aar包，引入至项目中

      ![image-20230819224613651](polar应用.assets/image-20230819224613651.png)![image-20230819224623187](polar应用.assets/image-20230819224623187.png)![image-20230819224642027](polar应用.assets/image-20230819224642027.png)

   4. 目前代码飘红问题已解决

      ![image-20230819224728440](polar应用.assets/image-20230819224728440.png)

5. 目前已经可以连接并获取到到心率，所以打算今晚睡觉带一晚，统计下今晚的睡觉的心率信息

   1. 先在界面上显示心率

      ![image-20230819232948308](polar应用.assets/image-20230819232948308.png)

   2. 画图使用 [开源库MPAndroidChart](https://weeklycoding.com/mpandroidchart/)

   3. [查看文档](https://weeklycoding.com/mpandroidchart-documentation/)

6. 









项目配置调整

https://blog.csdn.net/qq_41811862/article/details/121114332



https://blog.csdn.net/yangyulong0622/article/details/122179407