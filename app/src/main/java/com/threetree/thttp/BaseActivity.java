package com.threetree.thttp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.threetree.ttdialog.LoadingDialog;
import com.threetree.tthttp.viewbind.ILoadingView;

/**
 * Created by Administrator on 2018/7/25.
 */

public class BaseActivity extends AppCompatActivity implements ILoadingView {

    protected boolean mActive;
    LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActive = true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mActive = false;
    }

    @Override
    public void showLoading(boolean isCancel)
    {
        if(mDialog==null)
        {
            mDialog = new LoadingDialog.Builder(this)
                    .setCancelable(isCancel)
                    .setCancelOutside(isCancel)
                    .create();
        }
        mDialog.show();
    }

    @Override
    public void dismissLoading()
    {
        if(mDialog!=null)
            mDialog.dismiss();
    }

    @Override
    public boolean isActive()
    {
        return mActive;
    }


}
