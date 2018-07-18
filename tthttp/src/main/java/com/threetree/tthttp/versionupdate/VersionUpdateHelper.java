package com.threetree.tthttp.versionupdate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;

import java.io.File;

/**
 * Created by Administrator on 2018/7/16.
 */

public class VersionUpdateHelper implements ServiceConnection {

    private final String APK_SUFFIX = ".apk";

    private Context context;
    private VersionUpdateService service;
    private AlertDialog waitForUpdateDialog;
    private ProgressDialog progressDialog;

    private VersionUpdate versionUpdate;
    private boolean isCanceled;
    private OnUpdateResultListener mResultListener;
    private OnMustUpdateCancelListener mCancelListener;

    private boolean isCheckLoad;

    /**
     * 强制更新时，取消事件的监听
     */
    public interface OnMustUpdateCancelListener
    {
       void onCancel();
    }

    /**
     * 更新结果的监听
     */
    public interface OnUpdateResultListener
    {
        void onSuccess(String filePath);
        void onError(int code, String message);
    }

    public VersionUpdateHelper(Context context,VersionUpdate versionUpdate) {
        this.context = context;
        this.versionUpdate = versionUpdate;
    }

    public void resetCancelFlag() {
        isCanceled = false;
    }

    public void setResultListener(OnUpdateResultListener resultListener)
    {
        this.mResultListener = resultListener;
    }

    public void setCancelListener(OnMustUpdateCancelListener cancelListener)
    {
        this.mCancelListener = cancelListener;
    }

    /**
     * 是否需要检查，同名文件已经下载完成
     * true-检查同名文件是否已经下载完成，若完成则不再去下载该文件
     * false-不检查，每次都重新下载
     * @param isCheckLoad
     */
    public void setCheckLoad(boolean isCheckLoad)
    {
        this.isCheckLoad = isCheckLoad;
    }

    /**
     * 开始更新
     */
    public void startUpdateVersion() {
        if (isCanceled)
            return;
        if (isWaitForUpdate() || isWaitForDownload()) {
            return;
        }

        if(isCheckLoad && isDownLoad())
        {//已经下载完成apk，直接提示安装
            showInstallDialog();
            return;
        }

        if (service == null && context != null) {
            context.bindService(new Intent(context, VersionUpdateService.class), this, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 停止更新
     */
    public void stopUpdateVersion() {
        unBindService();
    }

    private void cancel() {
        isCanceled = true;
        unBindService();
    }

    private void unBindService() {
        if (isWaitForUpdate() || isWaitForDownload()) {
            return;
        }
        if (service != null && !service.isDownLoading()) {
            context.unbindService(this);
            service = null;
        }
    }

    private boolean isWaitForUpdate() {
        return waitForUpdateDialog != null && waitForUpdateDialog.isShowing();
    }

    private boolean isWaitForDownload() {
        return progressDialog != null && progressDialog.isShowing();
    }

    private void showNotWifiDownloadDialog() {
        final AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("下载新版本");
        builer.setMessage("检查到您的网络处于非wifi状态,下载新版本将消耗一定的流量,是否继续下载?");
        builer.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //exit app
                dialog.cancel();
                unBindService();
                if (versionUpdate.isMustUpdate()) {
                    //强制更新，不更新则退出应用
//                    GwApplication.getGwApplication().exit();
                    if(mCancelListener != null)
                    {
                        mCancelListener.onCancel();
                    }
                }
            }
        });
        builer.setPositiveButton("继续下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startDownLoad(versionUpdate.url,versionUpdate.fileDir,versionUpdate.fileName);
            }
        });
        builer.setCancelable(false);
        builer.show();
    }

    private void showInstallDialog()
    {
        final AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("版本升级");
        builer.setMessage(versionUpdate.content);
        //当点确定按钮时从服务器上下载新的apk 然后安装
        builer.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(mResultListener!=null)
                {
                    mResultListener.onSuccess(getFilePath());
                }
            }
        });

        //不用强制更新，可以取消
        if (!versionUpdate.isMustUpdate()) {
            builer.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    cancel();
                }
            });
        }
        builer.setCancelable(false);
        waitForUpdateDialog = builer.create();
        waitForUpdateDialog.show();
    }

    private void showDownloadDialog()
    {
        final AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("版本升级");
        builer.setMessage(versionUpdate.content);
        //当点确定按钮时从服务器上下载新的apk 然后安装
        builer.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (isWifi()) {
                    startDownLoad(versionUpdate.url,versionUpdate.fileDir,versionUpdate.fileName);
                } else {
                    showNotWifiDownloadDialog();
                }
            }
        });

        //不用强制更新，可以取消
        if (!versionUpdate.isMustUpdate()) {
            builer.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    cancel();
                }
            });
        }
        builer.setCancelable(false);
        waitForUpdateDialog = builer.create();
        waitForUpdateDialog.show();
    }

    public void startDownLoad(String url,String fileDir,String fileName)
    {
        service.startDownLoad(url,fileDir,fileName);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((VersionUpdateService.MyBinder) binder).getService();
        service.setIconRes(versionUpdate.iconRes);
        service.setmDownLoadListener(new VersionUpdateService.DownLoadListener() {
            @Override
            public void begin() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("正在下载更新");
                progressDialog.show();
            }

            @Override
            public void inProgress(int progress,float downloadSpeed) {
                if (progressDialog != null) {
                    progressDialog.setMax(100);

                    if(downloadSpeed > 1000)
                    {
                        progressDialog.setMessage("正在下载更新, "+ String.format("%.1f",downloadSpeed/1000) +"MB/s");
                    }else
                    {
                        progressDialog.setMessage("正在下载更新, "+ String.format("%.0f",downloadSpeed) +"KB/s");
                    }
                    progressDialog.setProgress(progress);
                }
            }

            @Override
            public void downLoadLatestSuccess() {
                if (progressDialog != null)
                    progressDialog.cancel();
                service.setDownLoading(false);
                unBindService();
                addSuffix();
                if(mResultListener!=null)
                {
                    mResultListener.onSuccess(getFilePath());
                }
            }

            @Override
            public void downLoadLatestFailed(int code,String message) {
                if (progressDialog != null)
                    progressDialog.cancel();
                service.setDownLoading(false);
                unBindService();
                if(mResultListener!=null)
                {
                    mResultListener.onError(code,message);
                }
            }
        });
        showDownloadDialog();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
        if (waitForUpdateDialog != null && waitForUpdateDialog.isShowing())
            waitForUpdateDialog.cancel();
        service = null;
        context = null;
    }

    private boolean isWifi()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null || cm.getActiveNetworkInfo() == null)
            return false;
        else
            return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    //添加后缀
    private void addSuffix()
    {
        String path = versionUpdate.fileDir+File.separator + versionUpdate.fileName;
        File file = new File(path);
        file.renameTo(new File(path + APK_SUFFIX));
    }

    //是否下载完成
    private boolean isDownLoad()
    {
        File file = new File(getFilePath());
        return file.exists();
    }

    //apk路径
    private String getFilePath()
    {
        return versionUpdate.fileDir+File.separator + versionUpdate.fileName + APK_SUFFIX;
    }


//    public void checkVersion(Version data,boolean isToast)
//    {
//        if(data != null)
//        {
//            String url = data.linkUrl;
//            if(TextUtils.isEmpty(url))
//            {
//                return;
//            }
//
//            VersionUpdate versionUpdate = new VersionUpdate();
//            int code = com.guawa.common.BuildConfig.VERSION_CODE;
//            boolean isNew = true;
//            if(!Utils.isEmpty(data.mustFlushVersion))
//            {
//                for (Integer version:data.mustFlushVersion)
//                {
//                    if(version == code)
//                    {
//                        isNew = false;
//                        break;
//                    }
//                }
//                versionUpdate.type = VersionUpdate.Type.MUSTUPDATE;
//            }else if(!Utils.isEmpty(data.flushVersion))
//            {
//                for (Integer version:data.flushVersion)
//                {
//                    if(version == code)
//                    {
//                        isNew = false;
//                        break;
//                    }
//                }
//                versionUpdate.type = VersionUpdate.Type.NEEDUPDATE;
//            }
//            if(isNew)
//            {
//                if(isToast)
//                {
//                    Toast.makeText(context,"当前已经是最新版本",Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//            versionUpdate.url = url;
//            Version.Config config = data.parse();
//            if(config != null && !TextUtils.isEmpty(config.tip))
//            {
//                versionUpdate.content = config.tip;
//            }
//            VersionUpdateHelper.resetCancelFlag();
//            setVersionUpdate(versionUpdate);
//            startUpdateVersion();
//        }
//    }
}
