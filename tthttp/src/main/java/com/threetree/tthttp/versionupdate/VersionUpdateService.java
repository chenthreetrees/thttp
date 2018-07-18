package com.threetree.tthttp.versionupdate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.threetree.tthttp.presenter.DownLoadPresenter;
import com.threetree.tthttp.viewbind.IProgressView;


/**
 * Created by Administrator on 2018/7/12.
 */

public class VersionUpdateService extends Service implements IProgressView {
    private final int NOTIFICATION_ID = 100;

    private boolean active;
    private boolean downLoading;
    private int mProgress;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private DownLoadListener mDownLoadListener;
    private DownLoadPresenter mPresenter;

    private int iconRes;

    //通过binder实现调用者client与Service之间的通信
    private MyBinder mBinder = new MyBinder();

    public interface DownLoadListener {
        void begin();
        void inProgress(int progress, float downloadSpeed);
        void downLoadLatestSuccess();
        void downLoadLatestFailed(int code, String message);
    }

    //client 可以通过Binder获取Service实例
    public class MyBinder extends Binder {
        public VersionUpdateService getService() {
            return VersionUpdateService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        active = true;
        mPresenter = new DownLoadPresenter(this,"http://www.baidu.com");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        active = false;
        if(mPresenter != null)
        {
            mPresenter.destroy();
        }
        stopDownLoadForground();
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        downLoading = false;
    }

    public void setmDownLoadListener(DownLoadListener downLoadListener) {
        this.mDownLoadListener = downLoadListener;
    }

    /**
     * 设置通知栏小图标
     * @param iconRes
     */
    public void setIconRes(int iconRes)
    {
        this.iconRes = iconRes;
    }

    /**
     * 开始下载
     * @param url
     * @param fileDir
     * @param fileName
     */
    public void startDownLoad(String url,String fileDir,String fileName)
    {
        mPresenter.setFileDir(fileDir);
        mPresenter.setDestFileName(fileName);
        mPresenter.download(url);
    }

    /**
     * 开始下载任务
     */
    private void doDownLoadTask() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        starDownLoadForground();
        downLoading = true;
        if (mDownLoadListener != null) {
            mDownLoadListener.begin();
        }
    }

    public boolean isDownLoading() {
        return downLoading;
    }

    public void setDownLoading(boolean downLoading) {
        this.downLoading = downLoading;
    }

    /**
     * 让Service保持活跃,避免出现:
     * 如果启动此服务的前台Activity意外终止时Service出现的异常(也将意外终止)
     */
    private void starDownLoadForground() {
        if(iconRes == 0)
        {
            throw new NullPointerException("smallIcon of notification is null");
        }
        CharSequence text = "下载中,请稍后...";
        mNotificationBuilder = new Notification.Builder(this);
        mNotificationBuilder.setSmallIcon(iconRes);  // the status icon
        mNotificationBuilder.setTicker(text);  // the status text
        mNotificationBuilder.setWhen(System.currentTimeMillis());  // the time stamp
        mNotificationBuilder.setContentText(text);  // the contents of the entry
        mNotificationBuilder.setContentTitle("正在下载更新" + 0 + "%"); // the label of the entry
        mNotificationBuilder.setProgress(100, 0, false);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(true);
        Notification notification = mNotificationBuilder.getNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopDownLoadForground() {
        stopForeground(true);
    }

    @Override
    public void start()
    {
        doDownLoadTask();
    }

    @Override
    public void error(int code, String message)
    {
        downLoading = false;
        if (mDownLoadListener != null) {
            mDownLoadListener.downLoadLatestFailed(code,message);
        }
    }

    @Override
    public void progress(long progress, long total,float speed)
    {
        mProgress = (int)(progress*100/total);
        if (mDownLoadListener != null) {
            mDownLoadListener.inProgress(mProgress,speed);
        }
        //频繁更新notification会使页面卡顿
        if(mProgress%3 == 0)
        {
            mNotificationBuilder.setContentTitle("正在下载更新" + mProgress + "%");
            mNotificationBuilder.setProgress(100, mProgress, false);
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.getNotification());
        }

        if (mProgress >= 100) {
            mNotificationManager.cancelAll();
        }
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    @Override
    public void success()
    {
        if (mDownLoadListener != null) {
            mDownLoadListener.downLoadLatestSuccess();
        }
        downLoading = false;
    }

    @Override
    public void toast(int code, String message)
    {

    }

    @Override
    public void showLoading(boolean isCancel)
    {

    }

    @Override
    public void dismissLoading()
    {

    }
}
