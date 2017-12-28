package com.jj.investigation.openfire.utils;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

/**
 * 文件保存
 * Created by ${R.js} on 2017/12/27.
 */

public class FileManager {

    public static File createFile(String filePath) {
        final File file = new File(Environment.getExternalStorageDirectory(), "js/" + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 生成一个UUID：该值不会重复
     */
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }


    /**
     * 根据文件名查找该文件是否存在
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
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                // 定位到项目所创文件夹位置
                File sFile = new File(externalStorageDirectory.getPath() + "/js/" + secondPaht + "/");
                if (sFile != null) {
                    // 获取该文件夹下的文件，因为该文件夹是自己所建，所以确定里面所有子项都是文件，所以不必须再
                    // 判断是否是文件夹和文件
                    File[] files = sFile.listFiles();
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
        Logger.e("resultt = " + result);
        return result;
    }

}
