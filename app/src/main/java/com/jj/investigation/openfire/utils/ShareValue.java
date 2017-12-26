package com.jj.investigation.openfire.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * 共享的存储数据类
 *
 * @author Eason
 */
public class ShareValue {

    public String TAG = "ShareValue";
    public Context context;// 上下文
    private SharedPreferences sp;// 共享数据操作类
    private static ShareValue pv;

    public ShareValue(Context c) {
        this.context = c;
        createPreferences(context);
    }

    /***
     * 单例模式
     **/
    public static ShareValue getInstance(Context c) {
        if (pv == null) {
            pv = new ShareValue(c);
        }
        return pv;
    }

    /***
     * 实例化
     **/
    public void createPreferences(Context context) {
        sp = context.getSharedPreferences(Constants.SP_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 取得String类型数据
     **/
    public String getStringValue(String xml) {
        if (sp == null) {
            createPreferences(context);
        }
        return sp.getString(xml, "");
    }

    /**
     * 存放String类型数据
     **/
    public void putStringValue(String xml, String str) {
        if (sp == null) {
            createPreferences(context);
        }
        Editor editor = sp.edit();
        editor.putString(xml, str);
        editor.commit();
    }

    /**
     * 得到一个整形数据
     **/
    public Integer getIntegerValue(String xml) {
        if (sp == null) {
            createPreferences(context);
        }
        return sp.getInt(xml, 1);
    }

    /**
     * 存放一个整形数据
     **/
    public void putIntegerValue(String xml, int i) {
        if (sp == null) {
            createPreferences(context);
        }
        Editor editor = sp.edit();
        editor.putInt(xml, i);
        editor.commit();
        Log.e(TAG, xml + "=" + i);
    }

    /**
     * 取得一个Boolean类型的数据
     **/
    public boolean getBooleanValue(String xml) {
        if (sp == null) {
            createPreferences(context);
        }
        return sp.getBoolean(xml, false);
    }

    /**
     * 存放一个boolean数据
     **/
    public void putBooleanValue(String xml, boolean isTrue) {
        if (sp == null) {
            createPreferences(context);
        }
        Log.e(TAG, xml + "=" + isTrue);
        Editor editor = sp.edit();
        editor.putBoolean(xml, isTrue);
        editor.commit();
    }

    /**
     * 得到一个整形数据
     **/
    public Long getLongValue(String xml) {
        if (sp == null) {
            createPreferences(context);
        }
        return sp.getLong(xml, 0);
    }

    /**
     * 存放一个整形数据
     **/
    public void putLongValue(String xml, Long i) {
        if (sp == null) {
            createPreferences(context);
        }
        Editor editor = sp.edit();
        editor.putLong(xml, i);
        editor.commit();
        Log.e(TAG, xml + "=" + i);
    }
}
