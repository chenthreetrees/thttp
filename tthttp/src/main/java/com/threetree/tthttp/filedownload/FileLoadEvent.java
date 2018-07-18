package com.threetree.tthttp.filedownload;

/**
 * Created by Administrator on 2018/7/12.
 */

public class FileLoadEvent {

    long total;
    long bytesLoaded;
    float speed;

    public long getBytesLoaded() {
        return bytesLoaded;
    }

    public long getTotal() {
        return total;
    }

    public float getSpeed()
    {
        return speed;
    }

    public FileLoadEvent(long total, long bytesLoaded,float speed) {
        this.total = total;
        this.bytesLoaded = bytesLoaded;
        this.speed = speed;
    }
}
