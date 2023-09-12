package com.youmehe.pagerprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youmehe.pagerprinter.myprinter.Global;
import com.youmehe.pagerprinter.myprinter.WorkService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BL_ADDR = "66:22:1F:D0:F9:5F";

    private final Handler printHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int result = msg.arg1;
            Log.e(TAG, result + "_" + msg.what);
            switch (msg.what) {
                case 109:
                    String address = BL_ADDR;
                    SharedPreferences settings = getSharedPreferences(Global.PREFERENCES_FILENAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Global.PREFERENCES_BTADDRESS, address);
                    editor.apply();
                    WorkService.workThread.connectBt(address);
                    findViewById(R.id.txt_print).setOnClickListener(view -> printMsg());
                    break;
                case Global.CMD_POS_PRINTPICTURERESULT:
                    Toast.makeText(getApplicationContext(), (result == 1) ? Global.toast_success
                            : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    break;
                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT:
                    Toast.makeText(getApplicationContext(), (result == 1) ? Global.toast_success
                            : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init bl
        initBlueTooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //小票机部分
    private void initBlueTooth() {
        WorkService.addHandler(printHandler);
        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Intent enableBlueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBlueToothIntent);
        } else {
            Log.e(TAG, "onCreate");
            BluetoothLeScanner bluetoothLeScanner = null;
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (result != null) {
                        Log.e(TAG,
                                "onScanResult" + result.getDevice().getName() + "|" + result.getDevice()
                                        .getAddress());
                        //if ("test".equals(result.getDevice().getName())) {
                        //  finalBluetoothLeScanner.stopScan(this);
                        //}
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.e(TAG, "onScanFailed" + errorCode);
                }
            });
        }

        //连接蓝牙部分
        printHandler.sendEmptyMessageDelayed(109, 1000);
    }

    private void printMsg() {
        // 不要直接和Pos打交道，要通过workThread来交流

        if (WorkService.workThread.isConnected()) {

            // TODO: 2020/5/7 根据昨天的了解，目前想到的有三种方案
            // TODO: 2020/5/7 方案一： 强行文字对齐，不支持包含英文(优先考虑)(使用压缩字体)
            // TODO: 2020/5/7 方案二： 相办法到处图片打印，效率会比较低(最后考虑)
            // TODO: 2020/5/7 方案三： 研究下网页打印部分内容(方案一不行再看)
            String text = "北京市朝阳区海河三鲜煮馍馆" + "\r\n";//店名超过16个字后会发生换行
            //由于目前是由压缩字体，所以一个汉字占用的数字宽度为 52/16=3.25
            //由于目前是由压缩字体，所以一个汉字占用的英文宽度为 42/16=2.625
            //英文符号的话正好42个
            text += "------------------------------------------\r\n";
            //汉字的话正好16个
            //第一列标题列内容总为：21+8*2.625=42
            //具体分布为：2 * 2.625 + 3 + 2 * 2.625 + 15 = 20.5
            text += "品名 x 数量               " + "单价" + "   总额" + "\r\n";
            //text += "饼子         " + "  1" + "     4" + "     4" + "\r\n";
            //品名
            String name = "素三鲜煮馍(普)";
            //数量
            String count = "2";
            //单价
            String price = "19";
            //总价
            String amount = "38";

            //根据中文计算长度
            float chineseLength = (float) (chineseCount(name) * 2.625);
            //总长度还需要加上左右小括号以及空格和x号
            float totalContentLength = chineseLength + 2 + 3;

            //todo 未知原因增加
            totalContentLength += 1;

            // TODO: 2020/5/9 测试时使用-来占位
            //String tag = "-";
            String tag = " ";

            String tmp = name + " x " + count;
            for (int i = 0; i < 28.5 - totalContentLength; i++) {
                tmp += tag;
            }

            String flagContent = "";
            for (int i = 0; i < 5 - price.length(); i++) {
                flagContent += tag;
            }
            price = flagContent + price;

            flagContent = "";
            for (int i = 0; i < 5 + 3 - amount.length(); i++) {
                flagContent += tag;
            }

            amount = flagContent + amount;

            text += tmp + price + amount + "\r\n";
            //text += "123456789022345678903234567890423456789052" + "\r\n";
            // TODO: 2021/10/2 for save paper 
            text = "测试  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n";
            text += "------------------------------------------\r\n\r\n";


            Bundle dataCP = new Bundle();
            Bundle dataAlign = new Bundle();
            Bundle dataRightSpace = new Bundle();
            Bundle dataLineHeight = new Bundle();
            Bundle dataTextOut = new Bundle();
            dataCP.putInt(Global.INTPARA1, 15);
            dataCP.putInt(Global.INTPARA2, 255);
            dataAlign.putInt(Global.INTPARA1, 0);
            dataRightSpace.putInt(Global.INTPARA1, 0);
            dataLineHeight.putInt(Global.INTPARA1, 32);
            dataTextOut.putString(Global.STRPARA1, text);
            //设置为utf-8时出现中文乱码
            dataTextOut.putString(Global.STRPARA2, "GBK");
            dataTextOut.putInt(Global.INTPARA1, 0);
            dataTextOut.putInt(Global.INTPARA2, 0);
            dataTextOut.putInt(Global.INTPARA3, 0);
            //控制字体大小部分：0:normal;1:压缩(中文不压缩，压缩的是英文32--->42；数字:42--->52);2:不指定:跟0一样的，没看出区别
            dataTextOut.putInt(Global.INTPARA4, 1);
            dataTextOut.putInt(Global.INTPARA5, 0x00);

            WorkService.workThread.handleCmd(Global.CMD_POS_SETCHARSETANDCODEPAGE, dataCP);
            WorkService.workThread.handleCmd(Global.CMD_POS_SALIGN, dataAlign);
            WorkService.workThread.handleCmd(Global.CMD_POS_SETRIGHTSPACE, dataRightSpace);
            WorkService.workThread.handleCmd(Global.CMD_POS_SETLINEHEIGHT, dataLineHeight);
            WorkService.workThread.handleCmd(Global.CMD_POS_STEXTOUT, dataTextOut);
        } else {
            Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
        }
    }

    private int chineseCount(String content) {
        int count = 0;
        for (char ch : content.toCharArray()) {
            count = isChinese(ch) ? count + 1 : count;
        }
        return count;
    }

    private boolean isChinese(char ch) {
        //获取此字符的UniCodeBlock
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        //  GENERAL_PUNCTUATION 判断中文的“号
        //  CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
        //  HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            System.out.println(ch + " 是中文");
            return true;
        }
        return false;
    }
}
