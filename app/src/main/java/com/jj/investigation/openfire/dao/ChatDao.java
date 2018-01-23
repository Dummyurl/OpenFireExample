package com.jj.investigation.openfire.dao;

import android.content.Context;

import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.impl.NetRequestRefreshListener;

import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 聊天界面
 * Created by ${R.js} on 2018/1/23.
 */

public class ChatDao extends BaseDao<NetRequestRefreshListener> {


    public ChatDao(Context context, NetRequestRefreshListener listener) {
        super(context, listener);
    }

    /**
     * 请求用户信息以及聊天记录
     */
    @Override
    public void requestData() {

    }

    /**
     * 发送文本消息
     */
    public void sendTxt() {

    }

    /**
     * 发送文件
     */
    public void sendFile(Map<String, RequestBody> fileMap) {
        api.upload(fileMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        System.out.println("上传失败：" + e.toString());
                        listener.onFailer(e.toString(), "", 0);
                    }

                    @Override
                    public void onNext(ServletData servletData) {
                        System.out.println("语音上传成功：" + servletData.toString());
                        listener.onSuccess(servletData, 0);
                    }
                });
    }
}
