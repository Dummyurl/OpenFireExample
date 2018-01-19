package com.jj.investigation.openfire;

import android.app.Application;

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

        //初始化Smack客户端
        new AndroidSmackInitializer().initialize();
        this.application = this;
    }

    public static Application getApplication() {
        return application;
    }
}
