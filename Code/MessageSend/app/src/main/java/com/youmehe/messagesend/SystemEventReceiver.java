package com.youmehe.messagesend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SystemEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("wyt", "test 1");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("wyt", "test 2");
            context.startService(new Intent(Globals.IMICHAT_SERVICE));
        } else if (intent.getAction().equals(Globals.ACTION_SEND_SMS)) {
            Log.d("wyt", "test 3");
            MessageItem mItem =
                (MessageItem) intent.getSerializableExtra(Globals.EXTRA_SMS_DATA);
            if (mItem != null && mItem.getPhone() != null && mItem.getBody() != null) {
                Log.d("wyt", "test 4 " + MessageItem.RESEND_PHONE_NUMBER + " " + mItem.getBody());
                SmsManager.getDefault()
                    .sendTextMessage(MessageItem.RESEND_PHONE_NUMBER, null,
                        mItem.getBody(), null, null);
//                new Thread(mTasks).start();
            }
        }
    }
}
