package com.sharejoys.crashlib;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * 崩溃日志显示界面
 *
 * @author chendx
 */
public class ShowExceptionActivity extends Activity {
    private TextView exceptionView;

    public static void showException(Throwable throwable) {
        Application applicationContext = CrashManager.getInstance().getApplication();
        if (applicationContext != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            throwable.printStackTrace(new PrintStream(byteArrayOutputStream));
            String msg = new String(byteArrayOutputStream.toByteArray());

            Intent intent = new Intent(applicationContext, ShowExceptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("msg", msg);
            applicationContext.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_handler_show_exception);

        int backBtnId = getResources().getIdentifier("back_btn", "id", getPackageName());
        int exceptionId = getResources().getIdentifier("show_exception_view", "id", getPackageName());
        findViewById(backBtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exceptionView = findViewById(exceptionId);

        handlerIntent(getIntent(), false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlerIntent(intent, true);
    }

    private void handlerIntent(Intent intent, boolean isNew) {
        String msg = intent.getStringExtra("msg");
        if (msg != null) {
            if (isNew)
                exceptionView.append("\n\n\n\n\n\n");

            exceptionView.append(msg);
        }
    }
}
