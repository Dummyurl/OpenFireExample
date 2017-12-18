package com.jj.investigation.openfire.bean;

/**
 * 联系人列表-联系
 * Created by ${R.js} on 2017/12/15.
 */

public class ContactChild {
    // 用户名
    private String userName;
    private String desc;
    // 用户唯一标志
    private String jid;
    /**
     * 当前用户好友列表中的好友的关系：
     * to：
     * both：两者互为好友
     * from：代表当前登录用户对该好友已经发送了好友申请，所以在好友列表中会显示这个好友，但是对方还未同意
     */
    private String type;

    public ContactChild(String userName, String desc, String jid) {
        super();
        this.userName = userName;
        this.desc = desc;
        this.jid = jid;
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
        return "ContactModel [userName=" + userName + ", jid=" + jid + "]";
    }

}
