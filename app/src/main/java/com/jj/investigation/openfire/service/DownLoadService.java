package com.jj.investigation.openfire.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jj.investigation.openfire.utils.Constants;
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
            Logger.e("action = " + intent.getAction());
            if (intent.getAction().equals(FILE_DOWNLOAD)) {
                Logger.e("通知下载2--DownloadReceiver");
                // 要下载的文件名称
                final String fileName = intent.getStringExtra("fileName");
                // 下载的地址URL
                final String url = Constants.DOWNLOAD_FILE + fileName;
                // 文件下载后要保存到手机的URL
                final File file = new File(FileManager.createFile("voice"), fileName);
                download(file, url);
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
                final File file = FileManager.downloadFile(url, dir);
                Logger.e("下载的文件：" + file.toString());
                // 下载完成后通知Activity刷新，更新状态
                final Intent intent = new Intent(FILE_DOWNLOAD_SUCCESS);
                intent.putExtra("fileName", dir.getName());
                sendBroadcast(intent);
            }
        }).start();
    }
}
