package com.youmehe.mediatry;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.JetPlayer;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

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
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
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
        setContentView(R.layout.activity_main);

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
        checkPermission();
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