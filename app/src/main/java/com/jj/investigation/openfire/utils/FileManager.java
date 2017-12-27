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

}
