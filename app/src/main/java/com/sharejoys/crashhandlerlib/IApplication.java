package com.sharejoys.crashhandlerlib;

import android.app.Application;
import android.content.Context;

import com.sharejoys.crashlib.CrashManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用Application
 * Created by chendx on 2017/5/18.
 */

public class IApplication extends Application {
    /**
     * 应用上下文
     */
    private static IApplication gInstance;

    /**
     * 获取应用上下文
     *
     * @return 应用上下文
     */
    public static IApplication getInstance() {
        return gInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (gInstance != null) {
            gInstance = this;
            return;
        }
        gInstance = this;
        CrashManager.getInstance().init(this, BuildConfig.DEBUG);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Manager {
    }

}
