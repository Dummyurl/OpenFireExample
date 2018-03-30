package com.jj.investigation.openfire.handler;

/**
 * Created by ${R.js} on 2018/2/24.
 */

public class JSLooper {

    static final ThreadLocal<JSLooper> sThreadLocal = new ThreadLocal<JSLooper>();
    final JSMessageQueue mQueue;

    private JSLooper() {
        mQueue = new JSMessageQueue();
    }

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one JSLooper may be created per thread");
        }
        sThreadLocal.set(new JSLooper());
    }


    public static JSLooper myLooper() {
        return sThreadLocal.get();
    }

    /**
     * 开始轮训
     */
    public static void loop() {
        final JSLooper looper = myLooper();
        final JSMessageQueue messageQueue = looper.mQueue;
        for (; ;) {
            JSMessage message = messageQueue.next();
            if (message == null) return;
            
        }
    }

}
