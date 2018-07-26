package com.threetree.thttp;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/28.
 * 拦截器，主要添加header，日志等操作
 */

public class HttpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("X-MMW-SIGN", "151c2875d04c2ebb890b8709d7722af8")
                .addHeader("X-MMW-VERSION", ""+ BuildConfig.VERSION_CODE)
                .addHeader("X-MMW-MECHINE", "2")
                .addHeader("MEMBERID", "1")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
