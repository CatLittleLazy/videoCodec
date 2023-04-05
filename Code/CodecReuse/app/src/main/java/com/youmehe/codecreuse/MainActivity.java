package com.youmehe.codecreuse;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.youmehe.codecreuse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'codecreuse' library on application startup.
    static {
        System.loadLibrary("codecreuse");
    }

    private ActivityMainBinding binding;

    private void requestmanageexternalstorage_Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
//                Toast.makeText(this, "2Android VERSION  R OR ABOVE，HAVE MANAGE_EXTERNAL_STORAGE GRANTED!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "1Android VERSION  R OR ABOVE，NO MANAGE_EXTERNAL_STORAGE GRANTED!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, 2);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestmanageexternalstorage_Permission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        // tv.setText(stringFromJNI());
        tv.setOnClickListener(view -> {
            selectFile();
        });
        binding.btnPlay.setOnClickListener(v -> {
            if (mFd != -1) {
                extractFromFd(mFd);
            } else {
                Toast.makeText(MainActivity.this, "未选择文件", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");       //   /*/ 此处是任意类型任意后缀
        //intent.setType(“audio/*”) //选择音频

        intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）

        //intent.setType(“video/*;image/*”)//同时选择视频和图片
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //没选择文件直接返回
        if (resultCode != RESULT_OK || requestCode != 100 || data == null) return;
        Uri uri = data.getData();
        String path = UriUtil.getPath(this, uri);
        binding.sampleText.setText(path);
        mFd = UriUtil.getFd(path);
        Log.e("wyt",  path + "-->" + mFd);
    }

    private int mFd = -1;

    /**
     * A native method that is implemented by the 'codecreuse' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void extractFromFd(int fd);

    public native void codecNow();
}