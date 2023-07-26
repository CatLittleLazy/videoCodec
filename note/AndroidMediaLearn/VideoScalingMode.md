# setVideoScalingMode

---

1. api[描述](https://developer.android.com/reference/android/media/MediaPlayer#setVideoScalingMode(int)), 可以看到16就已经提供了，算是一个很古老的api了, btw, 这个是MediaPlayer的接口

   ![image-20230726231354243](VideoScalingMode.assets/image-20230726231354243.png)

2. api[描述](https://developer.android.com/reference/android/media/MediaCodec.html#setVideoScalingMode(int))，MediaCodec中也存在这个接口

   ![image-20230726232607347](VideoScalingMode.assets/image-20230726232607347.png)

3. 目前支持的模式对应值为，需要注意的是，该值仅在视频宽高相同时起作用

   ![image-20230727002550590](VideoScalingMode.assets/image-20230727002550590.png)

   ![image-20230727002533377](VideoScalingMode.assets/image-20230727002533377.png)

4. 从MediaPlayer到MediaCodec

5. MediaCodec中的方法实现，从经验来判断最终应该会作用到format信息中，就是configure方法中的那个format，不过surface上可能也需要作用到

   1. jni调用

      ![image-20230726234355987](VideoScalingMode.assets/image-20230726234355987.png)

      ![image-20230726234425634](VideoScalingMode.assets/image-20230726234425634.png)

      ![image-20230726234450030](VideoScalingMode.assets/image-20230726234450030.png)

   2. JMediaCodec主要还是持有1个MediaCodec对象

      ![image-20230726234555248](VideoScalingMode.assets/image-20230726234555248.png)

   3. JMediaCodec中的实现

      ![image-20230726234900090](VideoScalingMode.assets/image-20230726234900090.png)

   4. 这里存在2次下发，先给surfaceTextureClient端下发，再给format下发

      1. format下发部分

         1. 之前看解码器复用时rotation逻辑时简单了解了下这里的逻辑，确实是更新了format中的信息，添加了该字段，可以看到最终其实还是会作用到surface上![image-20230727000143302](VideoScalingMode.assets/image-20230727000143302.png)

         2. 这里对应得是默认值为【VIDEO_SCALING_MODE_SCALE_TO_FIT】

            ![image-20230727000328311](VideoScalingMode.assets/image-20230727000328311.png)

      2. surfaceTextureClient下发部分

         1. 最终实在window上生效

            ![image-20230727001341401](VideoScalingMode.assets/image-20230727001341401.png)

         2. 实际上时支持4种的，未对外暴露的是0，3

            ![image-20230727001510569](VideoScalingMode.assets/image-20230727001510569.png)

         3. 

   5. 

6. 目前对该接口简单的了解了，那么我们在demo中进行调用(测试该值写入3，0可以，写入其他值会不显示最终crash)

   1. 设置为 VIDEO_SCALING_MODE_SCALE_TO_FIT 时 完全填充导致出现变形

      ![image-20230727010538471](VideoScalingMode.assets/image-20230727010538471.png)

      ![image-20230727010649062](VideoScalingMode.assets/image-20230727010649062.png)

      ![image-20230727010737296](VideoScalingMode.assets/image-20230727010737296.png)

   2. 设置为 VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING 时裁剪不拉伸

      ![image-20230727010927223](VideoScalingMode.assets/image-20230727010927223.png)

      ![image-20230727010832404](VideoScalingMode.assets/image-20230727010832404.png)

      ![image-20230727010855228](VideoScalingMode.assets/image-20230727010855228.png)

      

      

