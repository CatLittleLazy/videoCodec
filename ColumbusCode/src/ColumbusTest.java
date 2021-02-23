import java.awt.SystemTray;
import java.util.Arrays;

/**
 * Created by Administrator on 2021/2/22 23:07 description:
 */

class ColumbusTest {

  static int nStartBit = 0;

  public static void main(String[] args) {

    byte[] h264 = hexStringToByteArray(
        "00 00 00 01 67 64 00 15 AC D9 41 70 C6 84 00 00 03 00 04 00 00 03 00 F0 3C 58 B6 58".replace(
            " ", ""));
    nStartBit = 4 * 8;
    int forbidden_zero_bit = u(32, h264);
    System.out.println(Arrays.toString(h264));
    System.out.print(forbidden_zero_bit);
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] bs = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      bs[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16)
              << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return bs;
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

  public static void Ue(byte[] pBuffer) {
    //统计0的个数
    int nZeroNum = 0;
    while (nStartBit < 8) {
      if ((pBuffer[nStartBit / 8] & (0x80 >> nStartBit)) != 0) {
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
  }
}
