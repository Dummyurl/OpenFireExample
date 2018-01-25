package com.jj.investigation.openfire.utils;

import android.os.Environment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * 文件保存
 * Created by ${R.js} on 2017/12/27.
 */

public class FileManager {

    /**
     * 创建文件或者目录
     */
    public static File createFile(String filePath) {
        final File file = new File(Environment.getExternalStorageDirectory(), "js/" + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取指定文件的大小
     * @param file 文件
     */
    public static String getFileSize(File file) {
        String fileSize = "";
        String sufix = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            double size = fis.available();
            if (size < 1024) {
                sufix = "B";
            } else if (size >= 1024 && size < 1024 * 1024) {
                size = size / 1024;
                sufix = "KB";
            } else if (size >= 1024 * 1024) {
                size = size / 1024 /1024;
                sufix = "MB";
            }

            fileSize = Utils.doubFormat2(size) + sufix;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileSize;
    }

    /**
     * 获取文件的大小
     * @param filePath 文件的路径
     */
    public static String getFileSize(String filePath) {
        String fileSize = "";
        if (!Utils.isNull(filePath)) {
            File file = new File(filePath);
            fileSize = getFileSize(file);
        }

        return fileSize;
    }


    /**
     * 生成一个UUID：该值不会重复
     */
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }


    /**
     * 根据文件名查找该文件是否存在
     * mmp的，对文件的操作都忘光了快，直接file.exit就可以知道文件是否存在了，代码中有的也这么做了，
     * 但是不知道为啥脑子抽还自己写了一个方法
     *
     * @param fileName   文件名称(不是路径)
     * @param secondPaht 二级路径
     * @return 返回该文件的路径，如果需要读取该文件并用软件打开，可以用到
     */
    public static String searchFile(String fileName, String secondPaht) {
        String result = "";
        if (!Utils.isNull(fileName)) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                final File externalStorageDirectory = Environment.getExternalStorageDirectory();
                // 定位到项目所创文件夹位置
                final File sFile = new File(externalStorageDirectory.getPath() + "/js/" + secondPaht + "/");
                if (sFile != null) {
                    // 获取该文件夹下的文件，因为该文件夹是自己所建，所以确定里面所有子项都是文件，所以不必须再
                    // 判断是否是文件夹和文件
                    final File[] files = sFile.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file != null) {
                                if (file.getName().contains(fileName)) {
                                    result += file.getPath();
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param file    要上传的文件
     * @param url     上传的地址
     * @param handler 回调监听
     */
    public static void uploadFile(File file, String url, AsyncHttpResponseHandler handler) {
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        try {
            params.put("type", "voice");
            params.put("file", file);
            client.post(url, params, handler);
            System.out.println("路径：" + url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.e("上传文件异常：" + e.toString());
            ToastUtils.showLongToast("文件异常");
        }
    }

    /**
     * 下载文件
     *
     * @param url  要下载的文件的地址URL
     * @param file 下载后的文件（具体路径）
     * @return 返回下载的文件，即参数中中的file
     */
    public static File downloadFile(final String url, final File file) {
        // 如果该file已经存在，说明之前已经下载过，直接返回即可
        if (file.exists()) {
            return file;
        }
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            final URL downLoadUrl = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) downLoadUrl.openConnection();
            inputStream = conn.getInputStream();
            outputStream = new FileOutputStream(file);
            // 开始边读边写
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

}
