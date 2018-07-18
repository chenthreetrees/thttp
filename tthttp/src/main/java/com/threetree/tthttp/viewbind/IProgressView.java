package com.threetree.tthttp.viewbind;

/**
 * Created by Administrator on 2018/7/13.
 */

public interface IProgressView extends ILoadingView {
    void start();
    void error(int code, String message);
    void progress(long progress, long total, float speed);
    void success();
}
