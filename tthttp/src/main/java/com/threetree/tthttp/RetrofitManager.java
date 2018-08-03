package com.threetree.tthttp;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/7/12.
 */

public class RetrofitManager<T> {
    private static RetrofitManager mRetrofitManager;
    private String mBaseUrl;
    private Retrofit mRetrofit;
    private OkHttpClient.Builder mClientBuilder;
    private Retrofit.Builder mRetrofitBuilder;
    private T mService;
    private Class<T> mServiceClass;
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private boolean isReConnect;

    private RetrofitManager()
    {
        mClientBuilder = new OkHttpClient.Builder();
        mRetrofitBuilder = new Retrofit.Builder();
        mRetrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create());

        mConnectTimeout = 15;
        mReadTimeout = 20;
        mWriteTimeout =20;
        isReConnect = true;
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
        if(TextUtils.isEmpty(baseUrl))
            throw new NullPointerException("baseUrl must not be null");
        mRetrofitBuilder.baseUrl(baseUrl);
        mBaseUrl = baseUrl;
        return this;
    }

    public RetrofitManager serviceClass(Class<T> serviceClass)
    {
        if(serviceClass == null)
            throw new NullPointerException("serviceClass must not be null");
        mServiceClass = serviceClass;
        return this;
    }

    public RetrofitManager addConverterFactory(Converter.Factory factory)
    {
        mRetrofitBuilder.addConverterFactory(factory);
        return this;
    }

    public RetrofitManager connectTimeout(long connectTimeout)
    {
        mConnectTimeout = connectTimeout;
        return this;
    }

    public RetrofitManager readTimeout(long readTimeout)
    {
        mReadTimeout = readTimeout;
        return this;
    }

    public RetrofitManager writeTimeout(long writeTimeout)
    {
        mWriteTimeout = writeTimeout;
        return this;
    }

    public RetrofitManager retryOnConnectionFailure(boolean reConnect)
    {
        isReConnect = reConnect;
        return this;
    }

    public void create()
    {
        if(TextUtils.isEmpty(mBaseUrl))
            throw new NullPointerException("baseUrl must not be null");
        if(mServiceClass == null)
            throw new NullPointerException("service must not be null");

        mClientBuilder.connectTimeout(mConnectTimeout, TimeUnit.SECONDS);//超时
        mClientBuilder.readTimeout(mReadTimeout, TimeUnit.SECONDS);
        mClientBuilder.writeTimeout(mWriteTimeout, TimeUnit.SECONDS);
        mClientBuilder.retryOnConnectionFailure(isReConnect);//重连

        mRetrofitBuilder.client(mClientBuilder.build());
        mRetrofit = mRetrofitBuilder.build();
        mService = mRetrofit.create(mServiceClass);
    }

    public T getRetrofitService()
    {
        if (mRetrofit == null)
            throw new RuntimeException("retrofit must be init");
        if(mServiceClass == null)
            throw new NullPointerException("service must not be null");
        if(mService == null)
            mService = mRetrofit.create(mServiceClass);
        return mService;
    }
}
