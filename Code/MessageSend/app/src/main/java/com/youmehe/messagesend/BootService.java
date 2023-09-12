package com.youmehe.messagesend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service {
    public static final String TAG = "BootService";
    private ContentObserver mObserver;
    private Handler mHandler = new Handler();

    @Override

    public void onCreate() {

        Log.i(TAG, "onCreate().");

        super.onCreate();


        addSMSObserver();

        //设置点击跳转
        Context context = this;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String id = "1";
        String name = "happy message";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.setSound(null, null);
        notificationManager.createNotificationChannel(mChannel);
        notification = new Notification.Builder(context)
            .setChannelId(id)
            .setContentTitle("happy message")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
            .setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            .setSmallIcon(R.drawable.cute).build();
        startForeground(1, notification);
    }


    public void addSMSObserver() {

        Log.i(TAG, "add a SMS observer. ");


        ContentResolver resolver = getContentResolver();

        try (Cursor cursor = resolver.query(SMS.CONTENT_URI, new String[] {"_id"},
            null, null, SMS._ID + " desc")) {
            cursor.moveToNext();
            SMSObserver.MAX_ID = cursor.getInt(0);
            Log.e("wyt", "maxId = " + SMSObserver.MAX_ID);
        }

        Handler handler = new SMSHandler(this);

        mObserver = new SMSObserver(resolver, handler);

        resolver.registerContentObserver(SMS.CONTENT_URI, true, mObserver);

    }


    @Override

    public IBinder onBind(Intent intent) {

        return null;

    }


    @Override

    public void onDestroy() {

        Log.i(TAG, "onDestroy().");


        this.getContentResolver().unregisterContentObserver(mObserver);


        super.onDestroy();

        System.exit(0);

    }
}
