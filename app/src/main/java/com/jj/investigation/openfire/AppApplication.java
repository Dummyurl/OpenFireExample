package com.jj.investigation.openfire;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.jivesoftware.smack.android.AndroidSmackInitializer;

/**
 *
 * Created by ${R.js} on 2017/12/15.
 */
public class AppApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        this.application = this;

        //初始化Smack客户端
        new AndroidSmackInitializer().initialize();

        // 百度地图初始化
        SDKInitializer.initialize(getApplicationContext());
    }

    public static Application getApplication() {
        return application;
    }
}
