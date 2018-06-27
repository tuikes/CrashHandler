package com.sharejoys.crashhandlerlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sharejoys.crashlib.CrashManager;

public class MainActivity extends AppCompatActivity {
    private Button test;
    private Button test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cdx", test2.getText().toString());
            }
        });

        findViewById(R.id.share_crash_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将奔溃信息分享到第三方
                CrashManager.getInstance().shareCrashFile(MainActivity.this);
            }
        });
    }
}
