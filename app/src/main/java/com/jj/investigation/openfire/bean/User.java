package com.jj.investigation.openfire.bean;

import com.jj.investigation.openfire.utils.Utils;

import java.io.Serializable;

/**
 * 用户信息
 * Created by ${R.js} on 2017/12/25.
 */
public class User implements Serializable {

    // 用户ID
    private String id;
    // 该用户在OpenFire中的jid（他是用户的唯一标识）
    private String jid;
    // 注册使用的账号，一般是手机号（也可以是其他）
    private String username;
    // 密码
    private String plainPassword;
    // 昵称
    private String nickname;
    // 用户头像
    private String user_img;
    // 用户自己设置的简介或者是签名
    private String desc;
    // 是否在线
    private boolean isOnlion;


    public boolean isOnlion() {
        return isOnlion;
    }

    public void setOnlion(boolean onlion) {
        isOnlion = onlion;
    }

    public String getJid() {
        return Utils.isNull(jid) ? "" : jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        return Utils.isNull(nickname) ? "" : nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_img() {
        return Utils.isNull(user_img) ? "" : user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getId() {
        return Utils.isNull(id) ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Utils.isNull(username) ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlainPassword() {
        return Utils.isNull(plainPassword) ? "" : plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getDesc() {
        return Utils.isNull(desc) ? "" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", jid='" + jid + '\'' +
                ", username='" + username + '\'' +
                ", plainPassword='" + plainPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", user_img='" + user_img + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
