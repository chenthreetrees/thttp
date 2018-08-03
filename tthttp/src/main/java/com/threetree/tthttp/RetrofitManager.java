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
    private String mBaseUrl;
    private Retrofit mRetrofit;
    private OkHttpClient.Builder mClientBuilder;

    private RetrofitManager()
    {
        mClientBuilder = new OkHttpClient.Builder();
    }

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

    public RetrofitManager addInterceptor(Interceptor interceptor)
    {
        mClientBuilder.addInterceptor(interceptor);
        return this;
    }

    public RetrofitManager baseUrl(String baseUrl)
    {
        mBaseUrl = baseUrl;
        return this;
    }

    public void create()
    {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(mBaseUrl);
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());

        mClientBuilder.connectTimeout(15, TimeUnit.SECONDS);//超时
        mClientBuilder.readTimeout(20, TimeUnit.SECONDS);
        mClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
        mClientBuilder.retryOnConnectionFailure(true);//重连

        builder.client(mClientBuilder.build());
        mRetrofit = builder.build();
    }

    public <T> T getRetrofitService(Class<T> service)
    {
        if (mRetrofit == null)
            throw new RuntimeException("retrofit must be init");
        return mRetrofit.create(service);
    }
}
