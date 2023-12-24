# Native层如何获取应用信息

---

## 1. native层获取应用信息的方法

- 获取应用上下文，通过反向调用到PackageManager的方法获取

- 通过MediaMetricsService中提供的静态方法getSanitizedPackageNameAndVersionCode获取

  ![image-20231224084554503](Native层获取应用信息.assets/image-20231224084554503.png)

- 实际查看MediaMetrics提供的方法后，自然能够得出使用UidInfo提供的getInfo方法

  ![image-20231224084700774](Native层获取应用信息.assets/image-20231224084700774.png)



## 2. MediaMetricsService&&UidInfo方法尝试

1. 在MediaCodec.cpp中添加头文件

   ![image-20231224085111868](Native层获取应用信息.assets/image-20231224085111868.png)

2. 在MediaCodec目录下的Bp文件中找到“libstagefright”，在下方引入对应的头文件信息

   ![image-20231224085228343](Native层获取应用信息.assets/image-20231224085228343.png)

3. 编译报错如下，可以看到，此时MediaCodec已经找到了头文件，但是头文件中引用的BnMediaMetricsService.h没有找到，我们在系统中找一下文件位置

   ![image-20231224085315356](Native层获取应用信息.assets/image-20231224085315356.png)

4. 不难看出这个文件是过aidl时自动生成的

   ![image-20231224085721219](Native层获取应用信息.assets/image-20231224085721219.png)

5. 我们接着找一下BnMediaMetricsService在哪里出现过

   ![image-20231224094523413](Native层获取应用信息.assets/image-20231224094523413.png)

6. 可以看到主要是在libmediametrcis中，我们直接在该目录下的bp文件中找一下aidl-cpp

   ![image-20231224094713651](Native层获取应用信息.assets/image-20231224094713651.png)

7. 将其添加到MediaCodec中的bp文件对应位置继续编译

   ![image-20231224094829176](Native层获取应用信息.assets/image-20231224094829176.png)

8. 报错已更新

   ![image-20231224094857352](Native层获取应用信息.assets/image-20231224094857352.png)

9. 按照报错找一下位置

   ![image-20231224095029231](Native层获取应用信息.assets/image-20231224095029231.png)

10. 以此类推，失败的头文件引用全部添加后如下，编译通过

    ![image-20231224101043304](Native层获取应用信息.assets/image-20231224101043304.png)

    ![image-20231224101103894](Native层获取应用信息.assets/image-20231224101103894.png)

11. 实际添加代码后继续上报编译错误如下

    

12. 



![image-20231224220659108](Native层获取应用信息.assets/image-20231224220659108.png)



---

## #. 其他

- 如何打印 int64_t

  https://blog.csdn.net/hejianhua1/article/details/80780188

- 