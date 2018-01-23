package com.jj.investigation.openfire.dao;

import android.content.Context;

import com.jj.investigation.openfire.retrofit.RetrofitService;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;

/**
 * Dao层基类
 * Created by ${R.js} on 2018/1/23.
 */

public abstract class BaseDao<T> {

    public Context context;
    public T listener;
    public RetrofitService api;

    public BaseDao(Context context, T listener) {
        this.context = context;
        this.listener = listener;
        api = RetrofitUtil.createApi();
    }

    public abstract void requestData();

}
