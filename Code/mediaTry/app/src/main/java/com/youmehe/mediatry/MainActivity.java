package com.youmehe.mediatry;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        checkPermission();
//    }

    public void checkPermission() {
        if (checkSelfPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    private void requestmanageexternalstorage_Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Android VERSION  R OR ABOVE，HAVE MANAGE_EXTERNAL_STORAGE GRANTED!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Android VERSION  R OR ABOVE，NO MANAGE_EXTERNAL_STORAGE GRANTED!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, 2);
            }
        }
    }

    private Button startbtn;
    private Button stopbtn;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.content_main);

//        imageView = findViewById(R.id.img_show);
//        animationDrawable = (AnimationDrawable) imageView.getBackground();
//        //开始
//        startbtn = findViewById(R.id.btn_start);
//        startbtn.setOnClickListener(view -> {
////            animationDrawable.start();
//            tryPlayer();
//        });
//
//
//        //结束
//        stopbtn = findViewById(R.id.btn_stop);
//        stopbtn.setOnClickListener(view -> animationDrawable.stop());
//        checkPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestmanageexternalstorage_Permission();
    }

    public void tryPlayer() {
        for (int i = 0; i < 1; i++) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource("sdcard/videosource/128x128_h264_albumart.mp4");
                mediaPlayerList.add(mediaPlayer);
                mediaPlayer.setDisplay(null);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static List<MediaPlayer> mediaPlayerList = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != animationDrawable && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}