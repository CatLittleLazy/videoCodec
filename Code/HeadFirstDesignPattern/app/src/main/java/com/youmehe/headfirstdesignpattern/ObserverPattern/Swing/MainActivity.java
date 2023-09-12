package com.youmehe.headfirstdesignpattern.ObserverPattern.Swing;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.youmehe.headfirstdesignpattern.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.angel).setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "angel", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.devil).setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "devil", Toast.LENGTH_SHORT).show();
        });
    }
}