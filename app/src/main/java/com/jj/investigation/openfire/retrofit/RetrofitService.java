package com.jj.investigation.openfire.retrofit;

import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.bean.User;
import com.jj.investigation.openfire.utils.Constants;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * 接口
 * Created by ${R.js} on 2017/12/20.
 */

public interface RetrofitService {

    // 登录
    @FormUrlEncoded
    @POST(Constants.LOGIN)
    Observable<ServletData<User>> login(@Field("username") String username,
                                        @Field("password") String plainPassword);


    // 注册
    @FormUrlEncoded
    @POST(Constants.REGIST)
    Observable<ServletData> regist(@Field("username") String userPhone,
                                   @Field("password") String userPass,
                                   @Field("email") String email,
                                   @Field("jid") String jid);

    // 添加好友
    @FormUrlEncoded
    @POST(Constants.ADD_FRIEND)
    Observable<ServletData> addFriend(@Field("current_jid") String current_jid,
                                      @Field("friend_jid") String friend_jid);


    // 添加聊天记录
    @Multipart
    @POST(Constants.ADD_CHAT_RECORD)
    Observable<ServletData> addChatRecord(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(Constants.ADD_CHAT_RECORD)
    Observable<ServletData> addChatRecord(@Field("msg") String msg,
                                          @Field("msg_type") String msg_type,
                                          @Field("from_uid") String from_uid,
                                          @Field("to_uid") String to_uid,
                                          @Field("send_time") String send_time);


    // 查询聊天页面的用户资料
    @FormUrlEncoded
    @POST(Constants.CHAT_USERS_INFO)
    Observable<ServletData<ArrayList<User>>> getChatUsersInfo(@Field("jids") String uids);



}

