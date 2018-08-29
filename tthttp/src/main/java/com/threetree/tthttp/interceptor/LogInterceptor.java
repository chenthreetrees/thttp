package com.threetree.tthttp.interceptor;

import android.util.Log;

import com.threetree.tthttp.RetrofitManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/8/29.
 */

public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response originalResponse = chain.proceed(chain.request());
        if(RetrofitManager.getInstence().isDebug())
            Log.d("response",originalResponse.body().string());
        return originalResponse;
    }
}
