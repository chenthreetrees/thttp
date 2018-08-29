package com.threetree.thttp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.threetree.tthttp.RetrofitManager;
import com.threetree.tthttp.presenter.DownLoadPresenter;
import com.threetree.tthttp.viewbind.IProgressView;

public class MainActivity extends BaseActivity implements IDocView {

    ImageView mIv;
    DownLoadPresenter mDownLoadPresenter;
    DocPresenter mDocPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitManager.getInstence()
                .baseUrl("your baseUrl")
                .addInterceptor(new HttpInterceptor())
                .serviceClass(ApiService.class)
                .debug(true)
                .create();

        mDocPresenter = new DocPresenter(this,this);

        mIv = (ImageView)findViewById(R.id.iv);
        findViewById(R.id.load_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                download();
            }
        });

        findViewById(R.id.doc_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mDocPresenter.getDoc();
            }
        });

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mDownLoadPresenter!=null)
            mDownLoadPresenter.destroy();
        if(mDocPresenter != null)
            mDocPresenter.destroy();
    }

    @Override
    public void onSuccess(String doc)
    {
        Toast.makeText(this,doc,Toast.LENGTH_SHORT).show();
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
                    mDownLoadPresenter.destroy();
                }
            });
        }
        mDownLoadPresenter.setFileDir(FileUtil.ROOT_PATH);
        mDownLoadPresenter.setDestFileName(FileUtil.NAME);
        mDownLoadPresenter.download("http://upload.cbg.cn/2016/0726/1469533389366.jpg");
    }
}
