package com.threetree.tthttp.viewbind;

/**
 * Created by Administrator on 2018/4/2.
 */

public interface IBaseView {

    /**
     * 生命周期相关，异步线程完成时候，
     * 进行ui回调，判断下UI页面是否已经释放
     * 防止出现异常
     * @return
     */
    boolean isActive();

}
