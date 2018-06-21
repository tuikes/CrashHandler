package com.sharejoys.crashlib.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.PopupWindowCompat;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sharejoys.crashlib.CrashManager;
import com.sharejoys.crashlib.R;
import com.sharejoys.crashlib.util.CrashHelper;


/**
 * 崩溃日志显示界面
 *
 * @author chendx
 */
public class ShowExceptionActivity extends Activity {
    private static final String KEY_CRASH_INFO = "key_crash_info";
    private TextView exceptionView;

    public static void showException(Throwable throwable) {
        Application applicationContext = CrashManager.getInstance().getApplication();
        if (applicationContext != null) {
            Intent intent = new Intent(applicationContext, ShowExceptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(KEY_CRASH_INFO, CrashHelper.buildCrashInfo(throwable));
            applicationContext.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_handler_show_exception);
        setListener();
        int exceptionId = getResources().getIdentifier("show_exception_view", "id", getPackageName());
        exceptionView = findViewById(exceptionId);
        handlerIntent(getIntent(), false);
    }

    private void setListener() {
        int backBtnId = getResources().getIdentifier("back_btn", "id", getPackageName());
        int shareBtnId = getResources().getIdentifier("share_btn", "id", getPackageName());
        findViewById(backBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(shareBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashHelper.shareCrashFile(ShowExceptionActivity.this);
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
