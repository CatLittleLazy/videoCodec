package com.youmehe.messagesend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        sendMessage("test");
//        registerBroadcast();
//        startStaticBroadcast();
//        SMSHandler smsHandler = new SMSHandler(this);
//        SMSObserver smsObserver = new SMSObserver(getContentResolver() ,smsHandler);
//        smsObserver.onChange(true);
//        startService(new Intent(this, BootService.class));
        EditText editText = findViewById(R.id.edit_phone);
        MessageItem.RESEND_PHONE_NUMBER = editText.getHint().toString();
        findViewById(R.id.btn_start_service).setOnClickListener((view)->{
            startForegroundService(new Intent(this, BootService.class));
        });
        findViewById(R.id.btn_stop_service).setOnClickListener((view)->{
            stopService(new Intent(this, BootService.class));
        });
//        startService(new Intent(Globals.IMICHAT_SERVICE));
//        registerBroadcast();
//        senTextMailTest();

        Uri uri = ContentUris.withAppendedId(SMS.CONTENT_URI, 7997);

        // todo update read = 1
        Log.e("wyt", uri.toString());
        try (Cursor cursor = getContentResolver().query(uri, new String[]{"body", "read"}, null,null)){
            cursor.moveToNext();
            Log.e("wyt", cursor.getString(0) + "_" + cursor.getString(1));
        } catch (Exception e) {
            Log.e("wyt", "exception " + e.toString());
        }

        try (Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "read", "body"},
            "_id > 7996 and read = 0", null, "date desc")) {
            cursor.moveToFirst();
            Log.i("wyt","getCount = " + cursor.getCount());
            for(int i =0; i<cursor.getCount(); ++i) {
                Log.i("wyt","address = " + cursor.getLong(0) + "_" + cursor.getString(3));
//                if(cursor.getString(1).indexOf("136******")>=0) {//这里运用indexof方式查找，并没有用比较因为系统会有国际号码
                    ContentValues values = new ContentValues();
                    values.put("read", "1");        //修改短信为已读模式
                Log.i("wyt","update wyt before");
                    getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=?", new String[]{""+cursor.getInt(0)});
                Log.i("wyt","update wyt after");

//                    break;//这里如果跳出就只能更改一条（刚收到的可以这样实现而且手机上方将不再有短信提示）
//                }
                cursor.moveToNext();
            }
        }
    }

    public void senTextMailTest() {
        SendMailUtil.send("wangyutao@hihonor.com", "just cest");
    }

    public void sendFileMail() {
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"test.txt");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            String str = "hello world";
            byte[] data = str.getBytes();
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (os != null)os.close();
            } catch (IOException e) {
            }
        }
        SendMailUtil.send(file, "wangyutao@hihonor.com","file send");
    }


    // 动态接收广播
    private void registerBroadcast() {
        SystemEventReceiver systemEventReceiver = new SystemEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.ACTION_SEND_SMS);
//        filter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(systemEventReceiver, filter);
    }

    // 广播发送
    private void startStaticBroadcast() {
        Intent intent= new Intent();
        intent.setAction(Globals.ACTION_SEND_SMS);
        MessageItem messageItem = new MessageItem();
        messageItem.setPhone("18710710211");
        messageItem.setBody("test phone");
        messageItem.setId(1);
        messageItem.setProtocol(2);
        intent.putExtra(Globals.EXTRA_SMS_DATA, messageItem);
        sendBroadcast(intent);
    }

    private void sendMessage() {
        Uri uri = Uri.parse("smsto:10086");

        Intent it = new Intent(Intent.ACTION_SENDTO, uri);

        it.putExtra("sms_body", "Hello World！");

        startActivity(it);
    }

    private void sendMessage(String content) {
        //获取SmsManager
        SmsManager sms=SmsManager.getDefault();
        //如果内容大于70字，则拆分为多条
        List<String> texts=sms.divideMessage(content);

        //逐条发送短信
        for(String text:texts) {
            sms.sendTextMessage("18710710211", null, text, null, null);
        }
    }
}