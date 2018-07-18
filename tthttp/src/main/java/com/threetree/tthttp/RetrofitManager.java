package com.threetree.tthttp;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/7/12.
 */

public class RetrofitManager {
    private static RetrofitManager mRetrofitManager;

    public static RetrofitManager getInstence()
    {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null) {
                    mRetrofitManager = new RetrofitManager();
                }
            }
        }
        return mRetrofitManager;
    }

    public <T> T getRetrofitService(String baseUrl, Interceptor interceptor, Class<T> service)
    {
        OkHttpClient client;
        if (interceptor != null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)//超时
                    //添加应用拦截器
                    .addInterceptor(interceptor).build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)//超时
                    .build();
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                //将client与retrofit关联
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //到这一步创建完成
        T retrofitService = null;
        try {
            Class<T> className = (Class<T>) Class.forName(service.getName());
            retrofitService = retrofit.create(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrofitService;
    }
}
