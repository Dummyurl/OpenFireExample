package com.jj.investigation.openfire.bean;

/**
 * 联系人列表-联系
 * Created by ${R.js} on 2017/12/15.
 */

public class ContactChild {
    // 用户名
    private String userName;
    // 用户简介
    private String desc;
    // 用户唯一标志
    private String jid;
    // 是否在线
    private boolean isOnlion;


    public ContactChild(String userName, String desc, String jid) {
        super();
        this.userName = userName;
        this.desc = desc;
        this.jid = jid;
    }


    public boolean isOnlion() {
        return isOnlion;
    }

    public void setOnlion(boolean onlion) {
        isOnlion = onlion;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    public String toString() {
        return "ContactChild{" +
                "userName='" + userName + '\'' +
                ", desc='" + desc + '\'' +
                ", jid='" + jid + '\'' +
                ", isOnlion=" + isOnlion +
                '}';
    }
}
