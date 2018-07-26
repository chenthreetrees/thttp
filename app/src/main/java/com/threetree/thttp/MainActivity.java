package com.threetree.thttp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.threetree.tthttp.presenter.DownLoadPresenter;
import com.threetree.tthttp.viewbind.IProgressView;

public class MainActivity extends AppCompatActivity implements IProgressView{

    ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIv = (ImageView)findViewById(R.id.iv);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DownLoadPresenter presenter = new DownLoadPresenter(MainActivity.this,"http://www.baidu.com");
                presenter.setFileDir(FileUtil.ROOT_PATH);
                presenter.setDestFileName(FileUtil.NAME);
                presenter.download("http://upload.cbg.cn/2016/0726/1469533389366.jpg");

//                DocPresenter presenter = new DocPresenter(MainActivity.this);
//                presenter.getDoc();
            }
        });

    }

    @Override
    public void start()
    {
        Toast.makeText(this,"start",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error(int i, String s)
    {
        Toast.makeText(this,"error:" + s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void progress(long progress, long total, float v)
    {
        Toast.makeText(this,"progress:" + progress+"/"+total,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success()
    {
        mIv.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getFilePath()));
    }

    @Override
    public void showLoading(boolean b)
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public void toast(int i, String s)
    {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }
}
