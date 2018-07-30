package com.threetree.tthttp.presenter;


import com.threetree.tthttp.Result;
import com.threetree.tthttp.exception.ApiException;
import com.threetree.tthttp.exception.ExceptionEngine;
import com.threetree.tthttp.exception.ServerException;
import com.threetree.tthttp.viewbind.ILoadingView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/2.
 * 网络操作的处理器
 */
public class HttpPresenter extends BasePresenter {
    protected ILoadingView mLoadingView;
    public HttpPresenter(ILoadingView iLoadingView)
    {
         mLoadingView = iLoadingView;
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    public void destroy()
    {
        dispose();
    }

    public interface IHttpResultListener<T> {
        void onSuccess(T t);
        boolean onError(int code, String message);
    }

    public class HttpObserver<T> implements Observer<T> {
        IHttpResultListener<T> listener;

        public HttpObserver(IHttpResultListener<T> listener)
        {
            this.listener = listener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d)
        {
            addDisposable(d);
        }

        @Override
        public void onNext(@NonNull T t)
        {
            listener.onSuccess(t);
        }

        @Override
        public void onError(@NonNull Throwable e)
        {//这边可以统一拦截异常
            if(mLoadingView.isActive())
                mLoadingView.dismissLoading();
            if(e instanceof ApiException)
            {
                ApiException ex = (ApiException)e;
                //如果有特殊需求，拦截了异常
                if(ex.isServiceException())
                {
                    if(handleError(ex))
                        return;
                }
                if(!listener.onError(ex.getCode(),ex.getDisplayMessage()))
                {//如果不单独拦截，则统一弹出
                    showError(ex);
                }
            }
        }

        @Override
        public void onComplete()
        {
            if(mLoadingView.isActive())
                mLoadingView.dismissLoading();
        }
    }

    /**
     * 是否拦截异常
     * @param ex
     * @return
     */
    protected boolean handleError(ApiException ex)
    {
        return false;
    }

    /**
     * 显示异常，可以在这里使用log等
     * @param ex
     */
    protected void showError(ApiException ex)
    {

    }

    /**
     *
     * @param observable
     * @param listener
     * @param <T>
     */
    public <T>void subscribeHttp(Observable observable,IHttpResultListener<T> listener)
    {
        subscribeHttp(observable,listener,true);
    }

    /**
     * 发射HTTP请求(根据后台定义的返回结果转换数据)
     * @param observable
     * @param listener
     * @param isMapResult 是否需要转换数据
     * @param <T>
     */
    public <T>void subscribeHttp(Observable observable,IHttpResultListener<T> listener,boolean isMapResult)
    {
        if(isMapResult)
        {
            toSubscribe(onErrer(mapResult(observable)),listener);
        }else
        {
            toSubscribe(onErrer(observable),listener);
        }
    }


    /**
     * 处理结果转换
     * @param observable
     * @param <T>
     * @return
     */
    protected <T>Observable mapResult(Observable observable)
    {
        return observable.map(new Function<Result<T>, T>() {
            @Override
            public T apply(@NonNull Result<T> result) throws Exception
            {
                if (!result.success) {
                    throw new ServerException(result.errorCode, result.errorMessage);
                }
                return result.data;
            }
        });
    }

    /**
     * 处理异常
     * @param observable
     * @param <T>
     * @return
     */
    protected <T> Observable onErrer(Observable observable)
    {
        return observable.onErrorResumeNext(new Function<Throwable, Observable<T>>() {
            @Override
            public Observable<T> apply(@NonNull Throwable throwable) throws Exception
            {
                return Observable.error(ExceptionEngine.handleException(throwable));
            }
        });
    }

    /**
     * 线程切换
     * @param observable
     * @param listener
     * @param <T>
     */
    protected  <T>void toSubscribe(Observable observable,IHttpResultListener<T> listener)
    {
        HttpObserver<T> httpObserver = new HttpObserver<T>(listener);
        //设置事件触发在主线程
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
            //设置事件接受在UI线程以达到UI显示的目的
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpObserver);
    }

}
