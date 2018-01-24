package com.jj.investigation.openfire.utils;

import android.text.TextUtils;

/**
 * 日志
 * Created by ${R.js} on 2017/12/27.
 */

public class Logger {

    private static final String TAG = "WLW";

    /**
     * Set true or false if you want read logs or not
     */
    private static boolean logEnabled_v = true;
    private static boolean logEnabled_i = true;
    private static boolean logEnabled_e = true;

    public static void d() {
        if (logEnabled_v) {
            android.util.Log.v(TAG, getLocation());
        }
    }

    public static void d(String msg) {
        if (logEnabled_v) {
            android.util.Log.v(TAG, getLocation() + msg);
        }
    }

    public static void d(String msg,String msg2) {
        if (logEnabled_v) {
            android.util.Log.v(TAG, getLocation() + msg + msg2);
        }
    }

    public static void i(String msg) {
        if (logEnabled_i) {
            android.util.Log.i(TAG, getLocation() + msg);
        }
    }

    public static void i() {
        if (logEnabled_i) {
            android.util.Log.i(TAG, getLocation());
        }
    }

    public static void e(String msg) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation() + msg);
        }
    }

    public static void e(String msg,String msg2) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation() + msg + msg2);
        }
    }

    public static void e(String msg, Throwable e) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation() + msg, e);
        }
    }

    public static void e(Throwable e) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation(), e);
        }
    }

    public static void e() {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation());
        }
    }

    private static String getLocation() {
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":"
                                + trace.getMethodName() + ":"
                                + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }


}
