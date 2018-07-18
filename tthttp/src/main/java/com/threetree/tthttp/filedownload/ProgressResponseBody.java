package com.threetree.tthttp.filedownload;

import android.os.Message;

import com.threetree.tthttp.presenter.DownLoadPresenter;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Administrator on 2018/7/12.
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;
    DownLoadPresenter.PresenterHandler handler;
    public ProgressResponseBody(ResponseBody responseBody, DownLoadPresenter.PresenterHandler handler) {
        this.responseBody = responseBody;
        this.handler = handler;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0;
            long startTime = System.currentTimeMillis();
            @Override
            public long read(Buffer sink, long byteCount) throws IOException
            {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                float speed = 0;
                float useTime = (System.currentTimeMillis() - startTime)/1000;
                if(useTime > 0)
                {
                    speed = (bytesReaded/useTime)/1024;
                }
                //实时发送当前已读取的字节和总字节
                Message msg = new Message();
                msg.what = 1;
                msg.obj = new FileLoadEvent(contentLength(), bytesReaded,speed);
                handler.sendMessage(msg);
                return bytesRead;
            }
        };
    }
}

