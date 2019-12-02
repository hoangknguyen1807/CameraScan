package com.example.camerascan.imageeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static final String FOLDER_NAME = "aio_scanner";

    public static File createFolders(String path) {
        File baseDir;
        File aviaryFolder;
        if (path == null) {
            if (android.os.Build.VERSION.SDK_INT < 8) {
                baseDir = Environment.getExternalStorageDirectory();
            } else {
                baseDir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            }
            if (baseDir == null)
                return Environment.getExternalStorageDirectory();
            aviaryFolder = new File(baseDir, FOLDER_NAME);
        } else {
            aviaryFolder = new File(path);
        }

        if (aviaryFolder.exists())
            return aviaryFolder;
        if (aviaryFolder.isFile())
            aviaryFolder.delete();
        if (aviaryFolder.mkdirs())
            return aviaryFolder;
        return Environment.getExternalStorageDirectory();
    }

    public static File getEmptyFile(String path, String name) {
        File folder = FileUtils.createFolders(path);
        if (folder != null) {
            if (folder.exists()) {
                File file = new File(folder, name);
                return file;
            }
        }
        return null;
    }

    public static File genEditFile(String chosenPath) {
        return FileUtils.getEmptyFile(chosenPath,
                "edited-" + System.currentTimeMillis() + ".jpg");
    }

    public static boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static String saveBitmap(String bitName, Bitmap mBitmap) {
        File baseFolder = createFolders(null);
        File f = new File(baseFolder.getAbsolutePath(), bitName);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) { // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024d;
        int megaByte = (int) (kiloByte / 1024d);
        return megaByte + "MB";
    }

    /**
     * @Description:
     * @Author 11120500
     * @Date 2013-4-25
     */
    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

}
