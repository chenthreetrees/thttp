package com.threetree.thttp;

import android.content.Context;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/7/18.
 */

public class DocPresenter extends CommonPresenter {

    IDocView mDocView;
    public DocPresenter(Context context, IDocView iDocView)
    {
        super(context, iDocView);
        mDocView = iDocView;
    }

    /**
     * 网络请求的具体实现
     */
    public void getDoc()
    {
        if(mDocView.isActive())
            mDocView.showLoading(false);
        Observable observable = mApiService.getDoc(1,4);//调用接口
        subscribeHttp(observable, new IHttpResultListener<String>() {
            @Override
            public void onSuccess(String s)
            {
                if(mDocView.isActive())
                    mDocView.onSuccess(s);
            }

            @Override
            public boolean onError(int i, String s)
            {
                //错误或者异常处理，如果要拦截则返回true，否则框架统一处理
                return false;
            }
        });
    }
}
