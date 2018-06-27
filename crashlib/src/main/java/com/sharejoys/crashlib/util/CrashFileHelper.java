package com.sharejoys.crashlib.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CrashFileHelper {
    private final static String TAG = "CrashFileHelper";
    public static final String FILE_PROVIDER_NAME = "fileprovider";

    /**
     * Get sdcard path
     *
     * @return the path
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }




    /**
     * Get brandArray random file name according to current date
     *
     * @return brandArray random file name
     */
    public static String getRandomFileName() {
        Date todate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return sdf.format(todate);
    }


    /**
     * Create dir recursively
     *
     * @param path path name
     */
    public static void createDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 拍照路径 6.19
     */

    private static String FILE_NAME = "userIcon.jpg";
    public static String PATH_PHOTOGRAPH = "/smt/";

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static File getDCIMFile(String filePath, String imageName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File dirs = new File(Environment.getExternalStorageDirectory(),
                    "DCIM" + filePath);
            if (!dirs.exists())
                dirs.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),
                    "DCIM" + filePath + imageName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                }
            }
            return file;
        } else {
            return null;
        }

    }


    public static File saveBitmap2(Bitmap bitmap, String fileName, File baseFile) {
        FileOutputStream bos = null;
        File imgFile = new File(baseFile, "/" + fileName);
        try {
            bos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    public static File getBaseFile(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File f = new File(Environment.getExternalStorageDirectory(),
                    filePath);
            if (!f.exists())
                f.mkdirs();
            return f;
        } else {
            return null;
        }
    }

    public static String getFileName() {
        String fileName = FILE_NAME;
        return fileName;
    }

    /**
     * 由指定的路径和文件名创建文件
     */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 获取文件对应的URI
     * @param context
     * @param file
     * @return
     */
    public static Uri getUri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //data是file类型,忘了复制过来
            uri = FileProvider.getUriForFile(context, context.getPackageName() + "."+FILE_PROVIDER_NAME,
                    file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    /**
     * 压缩文件
     *
     * @param fs          需要压缩的文件
     * @param zipFilePath 被压缩后存放的路径
     * @return 成功返回 true，否则 false
     */
    public static boolean zipFiles(File fs[], String zipFilePath) {
        if (fs == null) {
            throw new NullPointerException("fs == null");
        }
        boolean result = false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
            for (File file : fs) {
                if (file == null || !file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    recursionZip(zos, file, file.getName() + File.separator);
                } else {
                    recursionZip(zos, file, "");
                }
            }
            result = true;
            zos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "zip file failed err: " + e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }


    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception {
        if (file.isDirectory()) {
            Log.i(TAG, "the file is dir name -->>" + file.getName() + " the baseDir-->>>" + baseDir);
            File[] files = file.listFiles();
            for (File fileSec : files) {
                if (fileSec == null) {
                    continue;
                }
                if (fileSec.isDirectory()) {
                    baseDir = file.getName() + File.separator + fileSec.getName() + File.separator;
                    Log.i(TAG, "basDir111-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                } else {
                    Log.i(TAG, "basDir222-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                }
            }
        } else {
            Log.i(TAG, "the file name is -->>" + file.getName() + " the base dir -->>" + baseDir);
            byte[] buf = new byte[2048];
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }

    /**
     * 文件大小（长度转化文字展示）
     *
     * @param var0 长度
     */
    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes"
                : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F)) + "KB"
                : (var0 < 1073741824L ? var2.format(
                (double) ((float) var0 / 1024.0F / 1024.0F)) + "MB"
                : (var0 < 0L ? var2.format(
                (double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }
}
