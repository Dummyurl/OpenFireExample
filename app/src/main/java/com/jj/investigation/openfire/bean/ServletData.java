package com.jj.investigation.openfire.bean;

/**
 * 查询后返回给客户端的JavaBean对象
 * Created by ${R.js} on 2017/12/25.
 */
public class ServletData {

    private int code;
    private String msg;
    private String type;
    private Object data;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public ServletData setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ServletData setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ServletData setData(Object data) {
        this.data = data;
        return this;
    }


    @Override
    public String toString() {
        return "ServletData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
