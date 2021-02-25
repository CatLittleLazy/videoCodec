import java.awt.SystemTray;
import java.util.Arrays;

/**
 * Created by Administrator on 2021/2/22 23:07 description:
 */

class ColumbusTest {

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
    //存疑 1080 -> 1920，解码出宽高位1088->1920；使用vlc打开发现还有一个缓冲分辨率 1088 x 1920：视频分辨率位1080 x 1920；目前估计跟16倍数有关
    //存疑 录制视频时 奇数分辨率会出现crash
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
