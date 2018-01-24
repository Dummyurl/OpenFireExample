package com.jj.investigation.openfire.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jj.investigation.openfire.utils.FileManager;
import com.jj.investigation.openfire.utils.Logger;

import java.io.File;

/**
 * 下载文件
 * Created by ${R.js} on 2018/1/18.
 */

public class DownLoadService extends Service {

    private DownloadReceiver receiver;
    // 下载的广播
    public static final String FILE_DOWNLOAD = "file_download";
    // 下载成功的广播
    public static final String FILE_DOWNLOAD_SUCCESS = "file_download_success";


    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
    }

    /**
     * 注册开始下载文件的广播
     */
    private void initReceiver() {
        receiver = new DownloadReceiver();
        final IntentFilter filter = new IntentFilter(FILE_DOWNLOAD);
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * 开始下载文件的广播，由Activity发送
     */
    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(FILE_DOWNLOAD)) {
                // 要下载的文件路径:就是网络下载的链接，同时还需要文件名称，来保存到本地
                final String fileName = intent.getStringExtra("loadUrl");
                // 保存到本地时的文件的父级目录
                final String localUrl = intent.getStringExtra("localUrl");
                // 文件下载后要保存到手机的URL
                String substring = fileName.substring(fileName.lastIndexOf("/"), fileName.length());
                // 下载后要保存的本地路径
                final File file = new File(FileManager.createFile(localUrl), substring);
                download(file, fileName);
            }
        }
    }

    /**
     * 开始下载文件
     *
     * @param dir 下载完成后文件存放的目录
     * @param url 文件的下载地址
     */
    private void download(final File dir, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.e("要下载的url = " + url);
                final File file = FileManager.downloadFile(url, dir);
                Logger.e("下载的文件：" + file.toString());
                // 下载完成后通知Activity刷新，更新状态
                final Intent intent = new Intent(FILE_DOWNLOAD_SUCCESS);
                intent.putExtra("fileName", dir.getPath());
                sendBroadcast(intent);
            }
        }).start();
    }
}
