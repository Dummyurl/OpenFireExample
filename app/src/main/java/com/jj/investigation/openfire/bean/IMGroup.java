package com.jj.investigation.openfire.bean;

/**
 * 群组信息
 * <p>
 * Created by ${R.js} on 2018/1/15.
 */
public class IMGroup {

    // 群组的jid
    private String jid;
    // 群组名称
    private String groupname;
    // 群组密码
    private String grouppassword;
    // 群组群成员数量
    private String groupnumber;
    // 群组简介
    private String gorupdesc;


    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupnumber() {
        return groupnumber;
    }

    public void setGroupnumber(String groupnumber) {
        this.groupnumber = groupnumber;
    }

    public String getGorupdesc() {
        return gorupdesc;
    }

    public void setGorupdesc(String gorupdesc) {
        this.gorupdesc = gorupdesc;
    }

    public String getGrouppassword() {
        return grouppassword;
    }

    public void setGrouppassword(String grouppassword) {
        this.grouppassword = grouppassword;
    }

    @Override
    public String toString() {
        return "IMGroup{" +
                "jid='" + jid + '\'' +
                ", groupname='" + groupname + '\'' +
                ", grouppassword='" + grouppassword + '\'' +
                ", groupnumber='" + groupnumber + '\'' +
                ", gorupdesc='" + gorupdesc + '\'' +
                '}';
    }
}
