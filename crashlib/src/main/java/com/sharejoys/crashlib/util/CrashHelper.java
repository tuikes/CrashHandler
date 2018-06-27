package com.sharejoys.crashlib.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.sharejoys.crashlib.CrashManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 异常捕获工具类  by zc 2018年05月07日15:24:15
 */
public final class CrashHelper {
    private static String defaultDir;
    private static String versionName;
    private static int versionCode;

    private static final String FILE_SEP = System.getProperty("file.separator");
    @SuppressLint("SimpleDateFormat")
    private static final Format FORMAT =
            new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");


    public static void init() {
        defaultDir = getDefaultCrashDir();
    }

    private CrashHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    static {
        try {
            PackageInfo pi = CrashManager.getInstance().getApplication()
                    .getPackageManager()
                    .getPackageInfo(CrashManager.getInstance().getApplication().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拼接奔溃信息
     */
    public static String buildCrashInfo(Throwable e) {
        final StringBuilder sb = new StringBuilder();
        final String time = FORMAT.format(new Date(System.currentTimeMillis()));
        final String head = "************* Log Head ****************"
                + "\nTime Of Crash      : "
                + time
                + "\nDevice Manufacturer: "
                + Build.MANUFACTURER
                + "\nDevice Model       : "
                + Build.MODEL
                + "\nAndroid Version    : "
                + Build.VERSION.RELEASE
                + "\nAndroid SDK        : "
                + Build.VERSION.SDK_INT
                + "\nApp VersionName    : "
                + versionName
                + "\nApp VersionCode    : "
                + versionCode
                + "\n************* Log Head ****************\n\n";
        sb.append(head);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.flush();
        sb.append(sw.toString());
        return sb.toString();
    }

    /**
     * 将奔溃信息存储到本地
     */
    public static void saveCrashLogToLocal(String crashInfo) {
        final String time = FORMAT.format(new Date(System.currentTimeMillis()));
        final String fullPath = defaultDir + time + ".txt";
        if (createOrExistsFile(fullPath)) {
            input2File(crashInfo, fullPath);
        } else {
            Log.e("CrashHelper", "create " + fullPath + " failed!");
        }
    }

    /**
     * get default crash log dir
     *
     * @return dir
     */
    private static String getDefaultCrashDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && CrashManager.getInstance().getApplication().getExternalCacheDir() != null) {
            return CrashManager.getInstance().getApplication().getExternalCacheDir()
                    + FILE_SEP
                    + "crash_log"
                    + FILE_SEP;
        } else {
            return CrashManager.getInstance().getApplication().getCacheDir()
                    + FILE_SEP
                    + "crash_log"
                    + FILE_SEP;
        }
    }

    private static void input2File(final String input, final String filePath) {
        Future<Boolean> submit =
                Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        BufferedWriter bw = null;
                        try {
                            bw = new BufferedWriter(new FileWriter(filePath, true));
                            bw.write(input);
                            onClearFile();
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        } finally {
                            try {
                                if (bw != null) {
                                    bw.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        try {
            if (submit.get()) return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("CrashHelper", "write crash info to " + filePath + " failed!");
    }

    private static boolean createOrExistsFile(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 删除超过2天的文件，防止过多
     */
    private static void onClearFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(getDefaultCrashDir());
            if (file.exists()) {
                if (file.listFiles().length > 3) {
                    int index = 0;
                    for (File temp : file.listFiles()) {
                        long time = System.currentTimeMillis() - temp.lastModified();
                        if (time / 1000 > 3600 * 24 * 2) {
                            temp.delete();
                            index++;
                        }
                        if (index >= file.listFiles().length - 3) break;
                    }
                }
            }
        }
    }

    //******************************************分享相关开始**********************************************
    private static final String LOG_ZIP_FILE_PATH = CrashHelper.getDefaultCrashDir() + File.separator + "crash_log.zip";

    public static void shareCrashFile(Activity activity) {
        File logPath = new File(CrashHelper.getDefaultCrashDir());
        if (!logPath.exists() || logPath.listFiles() == null || logPath.listFiles().length == 0) {
            Toast.makeText(activity,"没有崩溃日志可以反馈",Toast.LENGTH_SHORT).show();
            return;
        }
        if (logPath.listFiles().length == 1) {//只有一个崩溃文件则直接打开
            Uri uri = CrashFileHelper.getUri(activity, logPath.listFiles()[0]);
            shareFile(activity, uri, "text/plain");
        } else {//多个文件的话，压缩之后发送压缩包
            CrashFileHelper.zipFiles(logPath.listFiles(), LOG_ZIP_FILE_PATH);
            Uri uri = CrashFileHelper.getUri(activity, new File(LOG_ZIP_FILE_PATH));
            shareFile(activity, uri, "application/zip");
        }
    }


    /**
     * 分享文件
     *
     * @param fileUri  文件uri
     * @param fileType 文件类型（具体文件类型可参见本类下方注释）
     */
    private static void shareFile(Activity activity, Uri fileUri, String fileType) {
        //表示要创建一个发送指定内容的隐式意图
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //读取权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //指定发送的内容
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        //intent.putExtra(Intent.EXTRA_STREAM,uri);
        //指定发送内容的类型
        intent.setType(fileType);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //******************************************分享相关结束**********************************************

}
