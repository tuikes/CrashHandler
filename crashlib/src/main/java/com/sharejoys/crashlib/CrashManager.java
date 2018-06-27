package com.sharejoys.crashlib;

import android.app.Activity;
import android.app.Application;

import com.sharejoys.crashlib.ui.ExceptionCaughtAdapter;
import com.sharejoys.crashlib.util.CrashHelper;

/**
 * Created by chendx on 2018/5/2
 *
 * @since 1.0
 */

public class CrashManager {
    private static final CrashManager gInstance = new CrashManager();
    private Application application;
    private boolean isDebug = true;//是否是debug模式

    public static CrashManager getInstance() {
        return gInstance;
    }

    public Application getApplication() {
        return application;
    }

    public boolean isDebug() {
        return isDebug;
    }

    private CrashManager() {
    }

    public void init(Application application, boolean isDebug) {
        this.application = application;
        this.isDebug = isDebug;
        CrashHelper.init();
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionCaughtAdapter exceptionCaughtAdapter = new ExceptionCaughtAdapter(handler);
        Thread.setDefaultUncaughtExceptionHandler(exceptionCaughtAdapter);
    }

    /**
     * 调用该方法可以将奔溃信息文件分享到第三方，如微信等
     *
     * @param activity 调用分享的activity
     */
    public void shareCrashFile(Activity activity) {
        if (application == null) {
            throw new IllegalStateException("CrashManager尚未被初始化，请先调用init完成初始化！！！！");
        }
        CrashHelper.shareCrashFile(activity);
    }
}
