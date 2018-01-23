package com.jj.investigation.openfire.impl;

import com.jj.investigation.openfire.bean.ServletData;

/**
 * 请求网络的回调--上啦加载下拉刷新使用
 * Created by ${R.js} on 2018/1/23.
 */

public interface NetRequestRefreshListener {

    /**
     * 请求成功
     *
     * @param data 返回正常的数据
     */
    void onSuccess(ServletData data, int page);

    /**
     * 请求失败
     *
     * @param msg  返回失败的信息
     * @param type 以及失败的接口类型--即哪个接口请求失败了
     * @param page 当前请求的页数
     */
    void onFailer(String msg, String type, int page);

}
