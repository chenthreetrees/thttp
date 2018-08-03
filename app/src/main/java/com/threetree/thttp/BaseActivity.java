package com.threetree.thttp;

import android.support.v7.app.AppCompatActivity;

import com.threetree.tthttp.viewbind.ILoadingView;

/**
 * Created by Administrator on 2018/7/25.
 */

public class BaseActivity extends AppCompatActivity implements ILoadingView {

    @Override
    public void showLoading(boolean b)
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public boolean isActive()
    {
        return false;
    }


}
