/**
 * Created by Administrator on 2021/2/22 23:07 description:
 */

class ColumbusTest {

  //5->0000 0101

  public static void main(String[] args) {
    // 8位表示
    int nStartBit = 0;
    byte data = 2 & 0xFF; //字节上的5
    //统计0的个数
    int nZeroNum = 0;
    while (nStartBit < 8) {
      if ((data & (0x80 >> nStartBit)) != 0) {
        break;
      }
      nZeroNum++;
      nStartBit++;
    }
    nStartBit++;
    int dwRet = 0;
    for (int i = 0; i < nZeroNum; i++) {
      dwRet <<= 1;
      if ((data & (0x80 >> (nStartBit % 8))) != 0) {
        dwRet += 1;
      }
      nStartBit++;
    }
    int value = (1 << nZeroNum) - 1 + dwRet;
    System.out.println(value);
  }
}
