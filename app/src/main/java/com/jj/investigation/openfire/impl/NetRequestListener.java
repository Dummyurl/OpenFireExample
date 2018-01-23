package com.jj.investigation.openfire.impl;

import com.jj.investigation.openfire.bean.ServletData;

/**
 * 请求网络的回调
 * Created by ${R.js} on 2018/1/23.
 */

public interface NetRequestListener {

    /**
     * 请求成功
     * @param data 返回正常的数据
     */
    void onSuccess(ServletData data);

    /**
     * 请求失败
     * @param msg 返回失败的信息
     * @param type 败的接口类型 --即哪个接口请求失败了
     */
    void onFailer(String msg, String type);

}
