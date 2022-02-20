# 常用命令

### 0.官方文档

https://ffmpeg.org/ffmpeg.html

### 1.改变视频分辨率



### 2.添加视频封面图

要添加嵌入式封面/缩略图：

```
ffmpeg -i in.mp4 -i IMAGE -map 0 -map 1 -c copy -c:v:1 png -disposition:v:1 attach_pic out.mp4
```