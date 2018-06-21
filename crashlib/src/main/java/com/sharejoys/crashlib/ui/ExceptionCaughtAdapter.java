package com.sharejoys.crashlib.ui;

import com.sharejoys.crashlib.util.CrashHelper;

import java.lang.Thread.UncaughtExceptionHandler;


public class ExceptionCaughtAdapter implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    public ExceptionCaughtAdapter(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        CrashHelper.saveCrashLogToLocal(CrashHelper.buildCrashInfo(ex));
        ShowExceptionActivity.showException(ex);
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
