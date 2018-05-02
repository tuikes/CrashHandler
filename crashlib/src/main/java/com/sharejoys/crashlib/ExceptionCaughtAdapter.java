package com.sharejoys.crashlib;

import java.lang.Thread.UncaughtExceptionHandler;


public class ExceptionCaughtAdapter implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    public ExceptionCaughtAdapter(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ShowExceptionActivity.showException(ex);
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
