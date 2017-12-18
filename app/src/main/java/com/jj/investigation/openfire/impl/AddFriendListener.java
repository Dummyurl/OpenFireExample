package com.jj.investigation.openfire.impl;

/**
 * 添加好友的回调
 * Created by ${R.js} on 2017/12/18.
 */

public interface AddFriendListener {

    // 发送成功
    void sendSuccess();
    // 已经是好友，或者请求已经发送，不需要再次请求发送
    void sendFailed();

}
