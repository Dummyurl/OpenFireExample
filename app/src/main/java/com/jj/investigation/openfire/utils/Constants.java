package com.jj.investigation.openfire.utils;

/**
 * 常量
 * Created by ${R.js} on 2017/12/20.
 */

public class Constants {

    // 本地
//    public static final String BASE_URL = "http://192.168.1.126:8080/WebApplication/";

    // 阿里服务器
    public static final String BASE_URL = "http://39.106.120.166:8080/WebApplication/";

    // 注册
    public static final String REGIST = "registerServlet";
    // 登录
    public static final String LOGIN = "loginServlet";
    // 添加聊天记录
    public static final String ADD_CHAT_RECORD = "addChatRecord";
    // 获取聊天记录
    public static final String GET_CHAT_RECORD = "chatRecord";
    // 查询聊天页面的用户资料
    public static final String CHAT_USERS_INFO = "chatUserInfo";
    // 添加好友
    public static final String ADD_FRIEND = "addFriend";
    // 创建群组
    public static final String GROUP_CREATE = "groupCreate";

    // 路径如果是用Retrofit则不需要添加BaseURL
    // 上传文件
    public static final String UPLOAD_FILE = "uploadFile";
//    public static final String UPLOAD_FILE = BASE_URL + "uploadFile";
    // 下载文件--这个只是URL，没有对应的Servlet接口
    public static final String DOWNLOAD_FILE = BASE_URL + "upload/";




    // SharePreference
    public final static String SP_NAME = "sp_name";
    // 用户在自己平台的user_id
    public final static String SP_UID = "sp_uid";
    // 用户在OpenFire的ID
    public final static String SP_JID = "sp_jid";

}
