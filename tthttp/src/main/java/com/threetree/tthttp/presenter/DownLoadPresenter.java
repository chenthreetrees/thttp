package com.threetree.tthttp.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.threetree.tthttp.RetrofitManager;
import com.threetree.tthttp.RetrofitService;
import com.threetree.tthttp.filedownload.FileLoadEvent;
import com.threetree.tthttp.interceptor.ProgressInterceptor;
import com.threetree.tthttp.viewbind.IProgressView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/7/13.
 */

public class DownLoadPresenter extends HttpPresenter {

    private String destFileDir;
    private String destFileName;
    private IProgressView iProgressView;
    private RetrofitService mRetrofitService;
    private PresenterHandler mHandler = new PresenterHandler(this);;

    public static class PresenterHandler extends Handler {
        private WeakReference<DownLoadPresenter> mInstance;
        public PresenterHandler(DownLoadPresenter loadpresenter)
        {
            this.mInstance = new WeakReference<DownLoadPresenter>(loadpresenter);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            DownLoadPresenter presenter = mInstance == null ? null : mInstance.get();
            if (presenter == null) {
                return;
            }
            switch (msg.what)
            {
                case 1:
                    FileLoadEvent fileLoadEvent = (FileLoadEvent)msg.obj;
                    presenter.iProgressView.progress(fileLoadEvent.getBytesLoaded(),fileLoadEvent.getTotal(),fileLoadEvent.getSpeed());
                    break;
            }
        }
    }

    public DownLoadPresenter(IProgressView iProgressView, String baseUrl)
    {
        super(iProgressView);
        this.iProgressView = iProgressView;
        mRetrofitService = RetrofitManager.getInstence().getRetrofitService(baseUrl,new ProgressInterceptor(mHandler),RetrofitService.class);
    }

    /**
     * 设置下载的文件目录
     * @param fileDir
     */
    public void setFileDir(String fileDir)
    {
        destFileDir = fileDir;
    }

    /**
     * 设置下载的文件名（不能带后缀）
     * @param fileName
     */
    public void setDestFileName(String fileName)
    {
        destFileName = fileName;
    }

    @Override
    public void destroy()
    {
        super.destroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 下载
     * @param url 传入正确的URL，不能为空
     */
    public void download(String url)
    {
        if(TextUtils.isEmpty(url))
        {
           throw new NullPointerException("url is null!");
        }
        if(TextUtils.isEmpty(destFileDir))
        {
            throw new NullPointerException("FileDir is null!");
        }
        if(TextUtils.isEmpty(destFileName))
        {
            throw new NullPointerException("FileName is null!");
        }
        if(iProgressView.isActive())
            iProgressView.start();
        Observable observable = mRetrofitService.downloadFile(url);
        subscribeHttp(observable, new IHttpResultListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody)
            {
                if(responseBody != null && iProgressView.isActive())
                {
                    if(iProgressView.isActive())
                        iProgressView.success();
                }
            }

            @Override
            public boolean onError(int code, String message)
            {
                if(iProgressView.isActive())
                    iProgressView.error(code,message);
                return true;
            }
        },false);
    }

    @Override
    protected <T> void toSubscribe(Observable observable, IHttpResultListener<T> listener)
    {
        HttpObserver<T> httpObserver = new HttpObserver<T>(listener);
        //设置事件触发在主线程
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception
                    {
                        if(responseBody != null)
                            saveFile(responseBody);
                    }
                })
                //设置事件接受在UI线程以达到UI显示的目的
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpObserver);
    }

    private void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
    }
}
