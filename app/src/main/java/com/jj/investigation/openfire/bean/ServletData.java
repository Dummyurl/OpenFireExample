package com.jj.investigation.openfire.bean;

/**
 * 查询后返回给客户端的JavaBean对象
 * Created by ${R.js} on 2017/12/25.
 */
public class ServletData<T> {

    private int code;
    private String msg;
    private String type;
    private T data;


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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
