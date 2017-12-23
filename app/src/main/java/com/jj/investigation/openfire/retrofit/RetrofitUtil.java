package com.jj.investigation.openfire.retrofit;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.jj.investigation.openfire.BuildConfig;
import com.jj.investigation.openfire.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit工具类
 * 如果出现上传文件错误，例如：java.net.ProtocolException: unexpected end of stream
 * 请把添加日志拦截器的代码注释掉
 * Created by R.js on 2017/1/21.
 */
public class RetrofitUtil {

    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder builder;
    private static final String REQUEST_URL = "数据请求的URL = ";

    // 创建一个单例
    public static RetrofitService createApi() {
        if (retrofit == null) {
            synchronized (RetrofitUtil.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().client(getOkHttpClient())
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit.create(RetrofitService.class);
    }

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = getBuilder().build();
        }
        return okHttpClient;
    }

    public static OkHttpClient.Builder getBuilder() {
        if (builder == null) {
            builder = new OkHttpClient.Builder();
            // 添加日志拦截器
            addLogginInterceptor();
        }
        return builder;
    }

    /**
     * 添加日志拦截器
     */
    private static void addLogginInterceptor() {
        // 如果使用文件的提交进度，则需要注释掉该段代码
        if (BuildConfig.DEBUG) { // Retrofit日志拦截器--打印URL与返回的JSon字符串
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                // 日志：这个在发布时一定要注释掉
                @Override
                public void log(String message) {
                    Log.e(REQUEST_URL, message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }

}