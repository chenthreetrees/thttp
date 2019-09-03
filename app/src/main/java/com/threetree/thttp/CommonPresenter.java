package com.threetree.thttp;

import android.content.Context;
import android.widget.Toast;

import com.threetree.tthttp.RetrofitManager;
import com.threetree.tthttp.exception.ApiException;
import com.threetree.tthttp.presenter.HttpPresenter;
import com.threetree.tthttp.viewbind.ILoadingView;

/**
 * Created by Administrator on 2018/7/30.
 */

public class CommonPresenter extends HttpPresenter {
    protected ApiService mApiService;
    Context mContext;
    public CommonPresenter(Context context,ILoadingView iLoadingView)
    {
        super(iLoadingView);
        mContext = context;
        mApiService = (ApiService)RetrofitManager.getInstence().getRetrofitService();
    }

    @Override
    protected void showError(ApiException ex)
    {
        if(mLoadingView.isActive())
            Toast.makeText(mContext,"code:" + ex.getCode() + " msg:"+ex.getDisplayMessage(),Toast.LENGTH_SHORT).show();
    }
}
