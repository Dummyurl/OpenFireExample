package com.jj.investigation.openfire.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jj.investigation.openfire.AppApplication;

/**
 * 工具类
 * Created by ${R.js} on 2017/12/15.
 */

public class Utils {

    /**
     * 获取ApplicationContext
     */
    public static Context getContext() {
        return AppApplication.getApplication();
    }

    /**
     * 获取当前登录的用户ID:自己平台的user_id
     */
    public static String getUserId() {
        return ShareValue.getInstance(getContext()).getStringValue(Constants.SP_UID);
    }

    /**
     * 获取当前登录的用户的jid：在OpenFire的jid
     */
    public static String getJid() {
        return ShareValue.getInstance(getContext()).getStringValue(Constants.SP_JID);
    }

    /**
     * 如果这个字符串为空 返回:true
     */
    public static boolean isNull(String str) {
        if ("".equals(str) || str == null || "".equals("null")) {
            return true;
        }
        return false;
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    // 判断是否是WIFI环境
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    /***
     * 此方法只是关闭软键盘
     */
    public static void hintKbTwo(Context context, View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

}
