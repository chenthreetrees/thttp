package com.threetree.tthttp.interceptor;

import android.util.Log;

import com.threetree.tthttp.RetrofitManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/29.
 */

public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response response = chain.proceed(chain.request());
        MediaType mediaType = response.body().contentType();
        String content= response.body().string();
        if(RetrofitManager.getInstence().isDebug())
            Log.d("response",content);
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build();
    }
}
