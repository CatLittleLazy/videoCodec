package com.youmehe.messagesend;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

public class SMSHandler extends Handler

{

    public static final String TAG = "SMSHandler";

 

    private Context mContext;

   

    public SMSHandler(Context context)

    {

       super();

       this.mContext = context;

    }

   

    public void handleMessage(Message message)

    {

       Log.i(TAG,  "handleMessage: " + message);
       MessageItem item = (MessageItem) message.obj;

       //delete the sms

//       Uri uri = ContentUris.withAppendedId(SMS.CONTENT_URI, item.getId());

       // todo update read = 1
//        mContext.getContentResolver().delete(uri, null, null);

       Log.i(TAG,  "send sms item: " + item);

//        Intent intent= new Intent();
//        intent.setAction(Globals.ACTION_SEND_SMS);
//        intent.putExtra(Globals.EXTRA_SMS_DATA, item);
//        mContext.sendBroadcast(intent);
        // todo reSend message by SMS(0.1 rmb)
//        sendMessage(item.getBody());
        // todo reSend message by email(for free)
        SendMailUtil.send("wangyutao@hihonor.com", item.getBody());
    }

    private void sendMessage(String content) {
        //获取SmsManager
        SmsManager sms=SmsManager.getDefault();
        Log.d("wyt", "send " + MessageItem.RESEND_PHONE_NUMBER + " " + content);
        sms.sendTextMessage(MessageItem.RESEND_PHONE_NUMBER, null, content, null, null);

        //如果内容大于70字，则拆分为多条
//        List<String> texts=sms.divideMessage(content);

        //逐条发送短信
//        for(String text:texts) {
//            Log.d("wyt", "send " + MessageItem.RESEND_PHONE_NUMBER + " " + text);
//            sms.sendTextMessage(MessageItem.RESEND_PHONE_NUMBER, null, text, null, null);
//        }
    }
}