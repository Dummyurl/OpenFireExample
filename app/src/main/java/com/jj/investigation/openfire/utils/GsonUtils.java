package com.jj.investigation.openfire.utils;

import com.google.gson.Gson;


public class GsonUtils {

    private static Gson gson;

    public static Gson getGsonInstance() {
        if (gson == null) {
            synchronized (GsonUtils.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }
}
