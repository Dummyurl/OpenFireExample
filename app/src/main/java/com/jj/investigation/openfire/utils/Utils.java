package com.jj.investigation.openfire.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jj.investigation.openfire.AppApplication;

import java.text.DecimalFormat;

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

    /**
     * 判断是否是WIFI环境
     */
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


    /**
     * 隐藏软键盘
     */
    public static void hintKbTwo(Context context, View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    /**
     * 把一个数转成保留两位小数的数
     */
    public static String doubFormat2(Object object) {
        return new DecimalFormat("######0.00").format(object);
    }

    /**
     * 获取手机屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取手机屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 把字体大小转换成px
     */
    public static int sp2px(Context context, int spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * px转化成sp
     */
    public static int px2sp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }
}
