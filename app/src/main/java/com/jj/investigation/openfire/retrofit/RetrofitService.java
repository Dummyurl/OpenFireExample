package com.jj.investigation.openfire.retrofit;

import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.utils.Constants;

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
    Observable<ServletData> login(@Field("username") String username,
                                  @Field("password") String plainPassword);


    // 注册
    @FormUrlEncoded
    @POST(Constants.REGIST)
    Observable<ServletData> regist(@Field("username") String userPhone,
                                   @Field("password") String userPass,
                                   @Field("email") String email,
                                   @Field("jid") String jid);

    // 添加聊天记录
    @Multipart
    @POST(Constants.ADD_CHAT_RECORD)
    Observable<ServletData> getChatRecord(@PartMap Map<String, RequestBody> map);



    // 查询聊天页面的用户资料
    @FormUrlEncoded
    @POST(Constants.REGIST)
    Observable<ServletData> getChatUsersInfo(@Field("uids") String uids);

}

