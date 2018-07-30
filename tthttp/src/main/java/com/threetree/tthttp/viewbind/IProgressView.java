package com.threetree.tthttp.viewbind;

/**
 * Created by Administrator on 2018/7/13.
 */

public abstract class IProgressView implements ILoadingView {

    public abstract void start();
    public abstract void error(int code, String message);
    public abstract void progress(long progress, long total, float speed);
    public abstract void success();

    @Override
    public void showLoading(boolean isCancel)
    {

    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public void dismissLoading()
    {

    }
}
