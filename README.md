## videoCodec 杂七杂八

#### ODM 和 OEM

+ OBM：A设计，A生产，A品牌，A销售==工厂自己设计自产自销

+ ODM：B设计，B生产，A品牌，A销售==俗称“贴牌”，就是工厂的产品，别人的品牌

+ OEM：A设计，B生产，A品牌，A销售==代工，代生产，别人的技术和品牌，工厂只生产

#### frameworks/av/media/libmedia/xsd/Android.bp
+ xsd中未对 maxHFRFrameWidth声明
+ cs.android.com中直接搜到了bengal的media_profiles.xml文件
+ 目前来看文件内容一致 ht
hardware/qcom/sm7250/media/conf_files/bengal/media_profiles.xml

#### aosp in android studio
+ get the config file shell(if you have build source code)
	- source build/envsetup.sh
	- lunch [选择整编时选择的参数或者数字]
	- mmm development/tools/idegen/

+ get the config file shell(if you have not build source code)
	- source build/ensetup.sh  
	- make idegen

+ generate the config file
	-sudo development/tools/idegen/idegen.sh
	-sudo chmod 777 android.iml
	-sudo chmod 777 android.ipr

+ add this content to android.imp for avoiding see some we need not pay attention to

    >     <excludeFolder url="file://$MODULE_DIR$/bionic" />
    >     <excludeFolder url="file://$MODULE_DIR$/bootable" />
    >     <excludeFolder url="file://$MODULE_DIR$/build" />
    >     <excludeFolder url="file://$MODULE_DIR$/cts" />
    >     <excludeFolder url="file://$MODULE_DIR$/dalvik" />
    >     <excludeFolder url="file://$MODULE_DIR$/developers" />
    >     <excludeFolder url="file://$MODULE_DIR$/development" />
    >     <excludeFolder url="file://$MODULE_DIR$/device" />
    >     <excludeFolder url="file://$MODULE_DIR$/docs" />
    >     <excludeFolder url="file://$MODULE_DIR$/external" />
    >     <excludeFolder url="file://$MODULE_DIR$/hardware" />
    >     <excludeFolder url="file://$MODULE_DIR$/kernel" />
    >     <excludeFolder url="file://$MODULE_DIR$/out" />
    >     <excludeFolder url="file://$MODULE_DIR$/pdk" />
    >     <excludeFolder url="file://$MODULE_DIR$/platform_testing" />
    >     <excludeFolder url="file://$MODULE_DIR$/prebuilts" />
    >     <excludeFolder url="file://$MODULE_DIR$/sdk" />
    >     <excludeFolder url="file://$MODULE_DIR$/system" />
    >     <excludeFolder url="file://$MODULE_DIR$/test" />
    >     <excludeFolder url="file://$MODULE_DIR$/toolchain" />
    >     <excludeFolder url="file://$MODULE_DIR$/tools" />
    >     <excludeFolder url="file://$MODULE_DIR$/.repo" />

+ use this function to avoid android studio tips
    https://youtrack.jetbrains.com/articles/IDEA-A-2/Inotify-Watches-Limit?_ga=2.26749557.95062241.1621356487-591637766.1621356487
