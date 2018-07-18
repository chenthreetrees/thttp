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

    /**
     * 作为统一的吐司接口，
     * 也可以用来接收一些简单的string类型返回结果
     * @param code 根据code来区分业务类型
     * @param message
     */
    void toast(int code, String message);

}
