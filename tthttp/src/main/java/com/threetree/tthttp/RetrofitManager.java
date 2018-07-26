package com.threetree.tthttp;

import java.util.List;
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

    private OkHttpClient getHttpClient(List<Interceptor> interceptors)
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);//超时
        if(interceptors != null && interceptors.size() > 0)
        {
            for (Interceptor interceptor:interceptors)
            {
                builder.addInterceptor(interceptor);
            }
        }
        return builder.build();
    }

    private OkHttpClient getHttpClient(Interceptor interceptor)
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);//超时
        if(interceptor != null)
        {
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }


    public <T> T getRetrofitService(String baseUrl, List<Interceptor> interceptors, Class<T> service)
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                //将client与retrofit关联
                .client(getHttpClient(interceptors))
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

    public <T> T getRetrofitService(String baseUrl, Interceptor interceptor, Class<T> service)
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                //将client与retrofit关联
                .client(getHttpClient(interceptor))
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
