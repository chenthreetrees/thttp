package com.threetree.tthttp.viewbind;

/**
 * Created by Administrator on 2018/5/14.
 */

public interface ILoadingView extends IBaseView {
    /**
     * loading框是否阻塞式，即用户是否可以取消
     * @param isCancel
     */
    void showLoading(boolean isCancel);

    /**
     * 取消loading框
     */
    void dismissLoading();
}
