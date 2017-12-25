package com.jj.investigation.openfire.impl;

/**
 * 即时通讯的任务的回调监听
 * Created by ${R.js} on 2017/12/18.
 */

public interface TaskListener {

    void taskSucess(Object... params);
    void taskFailed(Object... params);

}
