package com.threetree.thttp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.threetree.ttdialog.LoadingDialog;
import com.threetree.tthttp.presenter.DownLoadPresenter;
import com.threetree.tthttp.viewbind.IProgressView;

public class MainActivity extends AppCompatActivity implements IDocView{

    ImageView mIv;
    DocPresenter mDocPresenter;
    DownLoadPresenter mDownLoadPresenter;
    boolean mActive;

    LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActive = true;
        mIv = (ImageView)findViewById(R.id.iv);
        mDocPresenter = new DocPresenter(this,this);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                download();

                mDocPresenter.getDoc();
            }
        });

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mActive = false;
        if(mDocPresenter!=null)
            mDocPresenter.destroy();
        if(mDownLoadPresenter!=null)
            mDownLoadPresenter.destroy();
    }

    private void download()
    {
        if(mDownLoadPresenter == null)
        {
            mDownLoadPresenter = new DownLoadPresenter(new IProgressView() {
                @Override
                public void start()
                {

                }

                @Override
                public void error(int code, String message)
                {

                }

                @Override
                public void progress(long progress, long total, float speed)
                {
                    Toast.makeText(MainActivity.this,"progress:" + progress+"/"+total + " speed:" + speed,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success()
                {
                    mIv.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getFilePath()));
                }
            }, "your baseUrl");
        }
        mDownLoadPresenter.setFileDir(FileUtil.ROOT_PATH);
        mDownLoadPresenter.setDestFileName(FileUtil.NAME);
        mDownLoadPresenter.download("http://upload.cbg.cn/2016/0726/1469533389366.jpg");
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

    @Override
    public void onSuccess(String doc)
    {

    }
}
