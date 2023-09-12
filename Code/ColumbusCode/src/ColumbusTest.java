import java.awt.SystemTray;
import java.util.Arrays;

/**
 * Created by Administrator on 2021/2/22 23:07 description:
 */

class ColumbusTest {


  // media_codecs_performance.xml部分作用会用于应用提升流畅度，主要针对于码流的处理
  static int nStartBit = 0;

  public static void main(String[] args) {

    byte[] h264 = hexStringToByteArray(
        "00 00 00 01 67 42 80 0A DA 02 20 79 EF 80 6D 0A 13 50 00 00 00 01 68 CE 06 F2 0A 00 00 00 01 67 42 80 0A DA 02 20 79 EF 80 6D 0A 13 50 00 00 00 01 68 CE 06 F2 00 00 00 01 65 B8 43 7F FF FC 3C 24 50 00 10 81 BB C9 C9 F8 7F FE 08 A2 80 00 80 05 56 38 00 08 3C 47 00"
            .replace(
                " ", ""));
    nStartBit = 4 * 8;
    int forbidden_zero_bit = u(1, h264);
    int nal_ref_idc = u(2, h264);
    int nal_unit_type = u(5, h264);
    System.out.println(nal_unit_type);
    if (nal_unit_type == 7) {
      int profile_idc = u(8, h264);
      //            当constrained_set0_flag值为1的时候，就说明码流应该遵循基线profile(Baseline profile)的所有约束.constrained_set0_flag值为0时，说明码流不一定要遵循基线profile的所有约束。
      int constraint_set0_flag = u(1, h264);//(h264[1] & 0x80)>>7;
      // 当constrained_set1_flag值为1的时候，就说明码流应该遵循主profile(Main profile)的所有约束.constrained_set1_flag值为0时，说明码流不一定要遵
      int constraint_set1_flag = u(1, h264);//(h264[1] & 0x40)>>6;
      //当constrained_set2_flag值为1的时候，就说明码流应该遵循扩展profile(Extended profile)的所有约束.constrained_set2_flag值为0时，说明码流不一定要遵循扩展profile的所有约束。
      int constraint_set2_flag = u(1, h264);//(h264[1] & 0x20)>>5;
      //            注意：当constraint_set0_flag,constraint_set1_flag或constraint_set2_flag中不只一个值为1的话，那么码流必须满足所有相应指明的profile约束。
      int constraint_set3_flag = u(1, h264);//(h264[1] & 0x10)>>4;
      //            4个零位
      int reserved_zero_4bits = u(4, h264);
      int level_idc = u(8, h264);

      //        0
      int seq_parameter_set_id = Ue(h264);
      // chroma_format_idc 的值应该在 0到 3的范围内（包括 0和 3）  yuv420  yuv422 yuv 444
      if (profile_idc == 100) {
        //                hight
        //                颜色位深   8  10  0
        int chroma_format_idc = Ue(h264);
        //                bit_depth_luma_minus8   视频位深   0 八位   1 代表10位
        int bit_depth_luma_minus8 = Ue(h264);
        int bit_depth_chroma_minus8 = Ue(h264);

        //                qpprime_y_zero_transform_bypass_flag    占用1个bit,当前使用到的字符为0xAC，  y轴 0标志位
        int qpprime_y_zero_transform_bypass_flag = u(1, h264);
        //               缩放换标志位
        int seq_scaling_matrix_present_flag = u(1, h264);
        //                D9
      }
      //            最大帧率
      int log2_max_frame_num_minus4 = Ue(h264);
      //确定播放顺序和解码顺序的映射
      int pic_order_cnt_type = Ue(h264);

      int log2_max_pic_order_cnt_lsb_minus4 = Ue(h264);
      //编码索引  码流顺序

      int num_ref_frames = Ue(h264);
      //
      int gaps_in_frame_num_value_allowed_flag = u(1, h264);

      System.out.println("------startBit " + nStartBit);
      int pic_width_in_mbs_minus1 = Ue(h264);
      System.out.println("------startBit " + nStartBit);
      int pic_height_in_map_units_minus1 = Ue(h264);
      System.out.println("======");// 宏快数
      int width = (pic_width_in_mbs_minus1 + 1) * 16;
      int height = (pic_height_in_map_units_minus1 + 1) * 16;
      System.out.println("width :  " + width + "   height: " + height);
    }
  }

  public static void main1(String[] args) {

    //新增vps，且宏块类型由32增加值64，之前5位，现在6位
    byte[] h265 = hexStringToByteArray(
        "42 01 01 01 60 00 00 03 00 B0 00 00 03 00 00 03 00 1E A0 02 20 80 1E 07 2F 96 BB 93 24 BA 80 2E D0 A1 28"
            .replace(
                " ", ""));
    nStartBit = 148; // ---> 18 * 8 + 4 = 148 位
    System.out.println("======");// 宏块数
    int width = Ue(h265);
    int height = Ue(h265);
    System.out.println("width :  " + width + "   height: " + height);
    //todo 存疑 1080 -> 1920，解码出宽高位1088->1920；使用vlc打开发现还有一个缓冲分辨率 1088 x 1920：视频分辨率位1080 x 1920；
    //todo 目前估计跟16倍数有关，已确定推测正确，部分手机中的配置文件会将1080写作1088
    //存疑 录制视频时 奇数分辨率会出现crash
    // 在media_codecs_google_video.xml中的encoder节点中会有对应编码器的最大支持分辨率，此处以pixel4a为例
    //MIMETYPE_VIDEO_VP8 = "video/x-vnd.on2.vp8";  √
    /*
    <MediaCodec name="OMX.google.vp8.encoder" type="video/x-vnd.on2.vp8">
            <!-- profiles and levels:  ProfileMain : Level_Version0-3 -->
            <Limit name="size" min="2x2" max="2048x2048" />
            <Limit name="alignment" value="2x2" />
            <Limit name="block-size" value="16x16" />
            <!-- 2016 devices can encode at about 10fps at this block count -->
            <Limit name="block-count" range="1-16384" />
            <Limit name="bitrate" range="1-40000000" />
            <Feature name="bitrate-modes" value="VBR,CBR" />
        </MediaCodec>
     */

    //MIMETYPE_VIDEO_VP9 = "video/x-vnd.on2.vp9";  √
    /*
    <MediaCodec name="OMX.google.vp9.encoder" type="video/x-vnd.on2.vp9">
            <!-- profiles and levels:  ProfileMain : Level_Version0-3 -->
            <Limit name="size" min="2x2" max="2048x2048" />
            <Limit name="alignment" value="2x2" />
            <Limit name="block-size" value="16x16" />
            <!-- 2016 devices can encode at about 8fps at this block count -->
            <Limit name="block-count" range="1-3600" /> <!-- max 1280x720 -->
            <Limit name="bitrate" range="1-40000000" />
            <Feature name="bitrate-modes" value="VBR,CBR" />
        </MediaCodec>
     */

    //MIMETYPE_VIDEO_AVC = "video/avc";            √
    /*
    <MediaCodec name="OMX.google.h264.encoder" type="video/avc">
            <!-- profiles and levels:  ProfileBaseline : Level41 -->
            <Limit name="size" min="16x16" max="2048x2048" />
            <Limit name="alignment" value="2x2" />
            <Limit name="block-size" value="16x16" />
            <Limit name="block-count" range="1-8192" /> <!-- max 2048x1024 -->
            <Limit name="blocks-per-second" range="1-245760" />
            <Limit name="bitrate" range="1-12000000" />
            <Feature name="intra-refresh" />
        </MediaCodec>
     */

    //MIMETYPE_VIDEO_MPEG4 = "video/mp4v-es";    176 x 144 √
    /*
    <MediaCodec name="OMX.google.mpeg4.encoder" type="video/mp4v-es">
            <!-- profiles and levels:  ProfileCore : Level2 -->
            <Limit name="size" min="16x16" max="176x144" />
            <Limit name="alignment" value="16x16" />
            <Limit name="block-size" value="16x16" />
            <Limit name="blocks-per-second" range="12-1485" />
            <Limit name="bitrate" range="1-64000" />
        </MediaCodec>
     */

    /*MIMETYPE_VIDEO_H263 = "video/3gpp";
    <MediaCodec name="OMX.google.h263.encoder" type="video/3gpp">
            <!-- profiles and levels:  ProfileBaseline : Level45 -->
            <Limit name="size" min="176x144" max="176x144" />
            <Limit name="alignment" value="16x16" />
            <Limit name="bitrate" range="1-128000" />
        </MediaCodec>
     */

    //在media_codecs_omx.xml中定义了如下编码器，需要注意
    // TODO: 2021/2/27  Non-Secure encoder  /  Secure encoder，应该是与一些参数的最大值有关
    // 对比vp8，发现min 与 max都变大了
    
    /*
    SM6150 Non-Secure encoder capabilities (Secure not supported)
 ______________________________________________________
 | Codec    | W       H       fps     Mbps    MB/s    |
 |__________|_________________________________________|
 | h264     | 4096    2160    24      100     829440  |
 | hevc     | 4096    2160    24      100     829440  |
 | vp8      | 3840    2160    30      100     972000  |
 |__________|_________________________________________|
     */

    // 同时规定了buffer通道为11个
    /*
    <Settings>
        <Setting name="max-video-encoder-input-buffers" value="11" />
    </Settings>
     */
    //MIMETYPE_VIDEO_HEVC = "video/hevc";          √
    /*
    <MediaCodec name="OMX.qcom.video.encoder.hevc" type="video/hevc" >
            <Quirk name="requires-allocate-on-input-ports" />
            <Quirk name="requires-allocate-on-output-ports" />
            <Quirk name="requires-loaded-to-idle-after-allocation" />
            <Limit name="size" min="96x96" max="4096x2160" />
            <Limit name="alignment" value="2x2" />
            <Limit name="block-size" value="16x16" />
            <Limit name="blocks-per-second" min="36" max="979200" />
            <Limit name="bitrate" range="1-100000000" />
            <Limit name="frame-rate" range="1-240" />
            <Limit name="concurrent-instances" max="16" />
            <Limit name="quality" range="0-100" default="80" />
            <Limit name="performance-point-4096x2160" value="24" />
            <Limit name="performance-point-3840x2160" value="30" />
            <Limit name="performance-point-1920x1080" value="120" />
            <Limit name="performance-point-1280x720" value="240" />
            <Feature name="bitrate-modes" value="VBR,CBR" />
        </MediaCodec>
     */

    //以下为未在xml中出现的编码器
    //MIMETYPE_VIDEO_MPEG2 = "video/mpeg2";
    //MIMETYPE_VIDEO_RAW = "video/raw";
    //MIMETYPE_VIDEO_DOLBY_VISION = "video/dolby-vision";
    //MIMETYPE_VIDEO_SCRAMBLED = "video/scrambled";
    /*
      todo 个人感觉出现      he.videoenocde: Invalid ID 0x00000000.时为不支持该解码器，待查看系统代码验证---暂未有结论
      I/libc: SetHeapTaggingLevel: tag level set to 0
      I/he.videoenocde: Late-enabling -Xcheck:jni
      I/he.videoenocde: Unquickening 12 vdex files!
      D/ApplicationLoaders: Returning zygote-cached class loader: /system/framework/android.test.base.jar
      D/NetworkSecurityConfig: No Network Security Config specified, using platform default
      D/NetworkSecurityConfig: No Network Security Config specified, using platform default
      E/he.videoenocde: Invalid ID 0x00000000.
      W/he.videoenocde: Accessing hidden method Landroid/view/View;->computeFitSystemWindows(Landroid/graphics/Rect;Landroid/graphics/Rect;)Z (greylist, reflection, allowed)
      W/he.videoenocde: Accessing hidden method Landroid/view/ViewGroup;->makeOptionalFitsSystemWindows()V (greylist, reflection, allowed)
      I/AdrenoGLES-0: QUALCOMM build                   : 0905e9f, Ia11ce2d146
         Build Date                       : 09/02/20
         OpenGL ES Shader Compiler Version: EV031.31.04.00
         Local Branch                     : gfx-adreno.lnx.2.0
         Remote Branch                    :
         Remote Branch                    :
         Reconstruct Branch               :
      I/AdrenoGLES-0: Build Config                     : S P 10.0.4 AArch64
      I/AdrenoGLES-0: Driver Path                      : /vendor/lib64/egl/libGLESv2_adreno.so
      I/AdrenoGLES-0: PFP: 0x016ee189, ME: 0x00000000
      W/AdrenoUtils: <ReadGpuID_from_sysfs:197>: Failed to open /sys/class/kgsl/kgsl-3d0/gpu_model
      W/AdrenoUtils: <ReadGpuID:221>: Failed to read chip ID from gpu_model. Fallback to use the GSL path
      I/Gralloc4: mapper 4.x is not supported
      W/MediaCodec-JNI: try to release MediaCodec from JMediaCodec::~JMediaCodec()...
      W/MediaCodec-JNI: done releasing MediaCodec from JMediaCodec::~JMediaCodec().
      D/AndroidRuntime: Shutting down VM
     */

  }

  public static byte[] hexStringToByteArray(String s) {
    //十六进制转byte数组
    int len = s.length();
    byte[] bs = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      bs[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return bs;
  }

  public static byte[] hexStringToByteArray1(String s) {
    int len = s.length();
    byte[] bs = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      bs[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return bs;
  }

  private static int u1(int bitIndex, byte[] h264) {
    //    位 索引  字节索引   bitIndex 32
    //            3           bitIndex位数
    //        0   100 0000
    //        nStartBit 开始 100换成10进制
    int dwRet = 0;
    for (int i = 0; i < bitIndex; i++) {
      dwRet <<= 1;
      if ((h264[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
        dwRet += 1;
      }
      nStartBit++;
    }
    return dwRet;
  }

  public static int u(int bitIndex, byte[] h264) {
    int dwRet = 0;
    for (int i = 0; i < bitIndex; i++) {
      dwRet <<= 1;
      if ((h264[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
        dwRet += 1;
      }
      nStartBit++;
    }
    return dwRet;
  }

  public static int Ue(byte[] pBuffer) {
    //统计0的个数
    int nZeroNum = 0;
    while (nStartBit < pBuffer.length * 8) {
      if ((pBuffer[nStartBit / 8] & (0x80 >> nStartBit % 8)) != 0) {
        break;
      }
      nZeroNum++;
      nStartBit++;
    }
    nStartBit++;
    int dwRet = 0;
    for (int i = 0; i < nZeroNum; i++) {
      dwRet <<= 1;
      if ((pBuffer[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
        dwRet += 1;
      }
      nStartBit++;
    }
    int value = (1 << nZeroNum) - 1 + dwRet;
    System.out.println(value);
    return value;
  }

  //    5
  public static int Ue1(byte[] pBuff) {
    //        5位  8位的表示
    //统计0 的个数
    int nZeroNum = 0;
    while (nStartBit < pBuff.length * 8) {
      if ((pBuff[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
        break;
      }
      nZeroNum++;
      nStartBit++;
    }

    nStartBit++;
    //跳出循环 到外面记录值  000 1  110      110
    //                    0001
    //        计算  101的十进制
    int dwRet = 0;//1  0
    for (int i = 0; i < nZeroNum; i++) {
      dwRet <<= 1;//0 <<1   1*2=2 11  0   3*2=6
      if ((pBuff[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
        dwRet += 1;//6+0 dwRet=6
      }
      nStartBit++;
    }
    int value = (1 << nZeroNum) - 1 + dwRet;
    System.out.println(value);
    return value;
  }
}
