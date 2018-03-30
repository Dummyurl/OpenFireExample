package com.jj.investigation.openfire.handler;

import android.os.Handler;
import android.os.Message;

/**
 * Created by ${R.js} on 2018/2/24.
 */

public class JSHandler {

    private JSLooper mLooper;
    private JSMessageQueue mMessageQueue;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

}
