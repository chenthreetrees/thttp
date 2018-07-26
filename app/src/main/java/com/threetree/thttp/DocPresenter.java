package com.threetree.thttp;

import com.threetree.tthttp.RetrofitManager;
import com.threetree.tthttp.presenter.HttpPresenter;
import com.threetree.tthttp.viewbind.ILoadingView;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/7/18.
 */

public class DocPresenter extends HttpPresenter {
    private ApiService mApi;
    public DocPresenter(ILoadingView iLoadingView)
    {
        super(iLoadingView);
        mApi = RetrofitManager.getInstence().getRetrofitService("http://mmw.guawaapp.com/mmw/api/",new HttpInterceptor(),ApiService.class);
    }

    public void getDoc()
    {
        Observable observable = mApi.getDoc(1,4);
        subscribeHttp(observable, new IHttpResultListener<String>() {
            @Override
            public void onSuccess(String s)
            {
                if(mLoadingView.isActive())
                    mLoadingView.toast(0,s);
            }

            @Override
            public boolean onError(int i, String s)
            {
                return false;
            }
        });
    }
}
