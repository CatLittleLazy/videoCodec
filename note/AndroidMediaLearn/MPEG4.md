# MPEG4文件及box添加读取

---

### 1.MPGE4

### 2.mp4 文件 box结构

### 3.如何在Android中为mp4文件添加新的box

1. 在video track中添加box

   ![image-20230928215314766](MPEG4.assets/image-20230928215314766.png)

2. 编译libstagefright.so替换后拍摄视频保存文件失败，查看日志，box名称只接受长度为4

   ![image-20230928220336510](MPEG4.assets/image-20230928220336510.png)

   ![image-20230928220401889](MPEG4.assets/image-20230928220401889.png)

3. 修改box名称：myBox --->  test后重新编译替换