package com.jj.investigation.openfire.handler;

/**
 * Created by ${R.js} on 2018/2/24.
 */

public class JSMessage {

    String what;
    JSHandler target;

    public JSMessage(String what) {
        this.what = what;
    }

    @Override
    public String toString() {
        return "JSMessage{" +
                "what='" + what + '\'' +
                ", target=" + target +
                '}';
    }
}
