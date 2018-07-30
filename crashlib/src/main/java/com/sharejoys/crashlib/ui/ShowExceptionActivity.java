package com.sharejoys.crashlib.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sharejoys.crashlib.CrashManager;
import com.sharejoys.crashlib.R;


/**
 * 崩溃日志显示界面
 *
 * @author chendx
 */
public class ShowExceptionActivity extends Activity {
    private static final String KEY_CRASH_INFO = "key_crash_info";
    private TextView exceptionView;

    public static void showException(String crashInfo) {
        Application applicationContext = CrashManager.getInstance().getApplication();
        if (applicationContext != null) {
            Intent intent = new Intent(applicationContext, ShowExceptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(KEY_CRASH_INFO, crashInfo);
            applicationContext.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_lib_activity_show_exception);
        setListener();
        int exceptionId = getResources().getIdentifier("crash_lib_activity_show_exception_view", "id", getPackageName());
        exceptionView = findViewById(exceptionId);
        handlerIntent(getIntent(), false);
    }

    private void setListener() {
        int backBtnId = getResources().getIdentifier("crash_lib_activity_back_btn", "id", getPackageName());
        int shareBtnId = getResources().getIdentifier("crash_lib_activity_share_btn", "id", getPackageName());
        findViewById(backBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(shareBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashManager.getInstance().shareCrashFile(ShowExceptionActivity.this);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlerIntent(intent, true);
    }

    private void handlerIntent(Intent intent, boolean isNew) {
        String msg = intent.getStringExtra(KEY_CRASH_INFO);
        if (msg != null) {
            if (isNew)
                exceptionView.append("\n\n\n\n\n\n");
            exceptionView.append(msg);
        }
    }
}
