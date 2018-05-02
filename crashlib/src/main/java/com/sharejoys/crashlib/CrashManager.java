package com.sharejoys.crashlib;

import android.app.Application;

/**
 * Created by chendx on 2018/5/2
 *
 * @since 1.0
 */

public class CrashManager {
    private static final CrashManager gInstance = new CrashManager();
    private Application application;

    public static CrashManager getInstance() {
        return gInstance;
    }

    public Application getApplication() {
        return application;
    }

    private CrashManager() {
    }

    public void init(Application application) {
        this.application = application;
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        ExceptionCaughtAdapter exceptionCaughtAdapter = new ExceptionCaughtAdapter(handler);
        Thread.setDefaultUncaughtExceptionHandler(exceptionCaughtAdapter);
    }

}
