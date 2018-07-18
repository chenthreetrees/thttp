package com.threetree.tthttp.interceptor;


import com.threetree.tthttp.filedownload.ProgressResponseBody;
import com.threetree.tthttp.presenter.DownLoadPresenter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/7/12.
 */

public class ProgressInterceptor implements Interceptor {

    DownLoadPresenter.PresenterHandler handler;
    public ProgressInterceptor(DownLoadPresenter.PresenterHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(),handler))
                .build();
    }
}
