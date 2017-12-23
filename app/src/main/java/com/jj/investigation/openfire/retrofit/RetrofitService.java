package com.jj.investigation.openfire.retrofit;

import com.jj.investigation.openfire.bean.HttpRequestResult;
import com.jj.investigation.openfire.bean.ServeTest;
import com.jj.investigation.openfire.bean.User;
import com.jj.investigation.openfire.utils.Constants;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 接口
 * Created by ${R.js} on 2017/12/20.
 */

public interface RetrofitService {

    @FormUrlEncoded
    @POST(Constants.LOGIN_URL)
    Observable<User> login(@Field("username") String username,
                           @Field("password") String plainPassword);


    @FormUrlEncoded
    @POST(Constants.REGIST_URL)
    Observable<HttpRequestResult> regist(@Field("userPhone") String userPhone,
                                         @Field("userPass") String userPass);


    @GET(Constants.BASE_URL + "abc")
    Observable<ServeTest> test();

}
