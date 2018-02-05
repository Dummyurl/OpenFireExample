package com.jj.investigation.openfire.impl;

import com.jj.investigation.openfire.bean.MyMessage;

/**
 * 聊天界面消息发送或者接收错误的点击事件
 * Created by ${R.js} on 2018/2/1.
 */

public interface ChatMsgErrorClickListener {

    void msgErrorClick(MyMessage message);

}
