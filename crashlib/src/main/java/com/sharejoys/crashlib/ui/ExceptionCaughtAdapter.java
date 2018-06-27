package com.sharejoys.crashlib.ui;

import com.sharejoys.crashlib.CrashManager;
import com.sharejoys.crashlib.util.CrashHelper;

import java.lang.Thread.UncaughtExceptionHandler;


public class ExceptionCaughtAdapter implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    public ExceptionCaughtAdapter(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String crashInfo = CrashHelper.buildCrashInfo(ex);
        CrashHelper.saveCrashLogToLocal(crashInfo);
        if (CrashManager.getInstance().isDebug()) {//debug模式下才显性的展示崩溃信息
            ShowExceptionActivity.showException(crashInfo);
        }
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
