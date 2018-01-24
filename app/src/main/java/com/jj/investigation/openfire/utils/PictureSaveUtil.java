package com.jj.investigation.openfire.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 保存图片的工具类
 * Created by ${R.js} on 2017/6/19.
 */

public class PictureSaveUtil {
    /**
     * 首先默认文件保存路径
     */
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().
            equalsIgnoreCase(Environment.MEDIA_MOUNTED) ?
            Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/js/img";//保存的确切位置

    /**
     * 保存图片的方法
     * @param bm       要保存的BItmap对象
     * @param fileName 图片名称
     * @param path     子文件夹路径：使用中可传空（不是null）
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String fileName, String path) throws IOException {
        String subForder = SAVE_REAL_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        // 发送广播通知系统图片库更新图片
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        Utils.getContext().sendBroadcast(intent);
    }
}
