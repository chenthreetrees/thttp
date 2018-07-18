package com.threetree.tthttp.versionupdate;

/**
 * Created by Administrator on 2018/7/16.
 */

public class VersionUpdate {
    public String content;//更新提示框的内容
    public int type;
    public String url;
    public String fileDir;
    public String fileName;
    public int iconRes;//通知栏显示的小图标

    public class Type
    {
        public static final int MUSTUPDATE = 1;//强更
        public static final int NEEDUPDATE = 2;//非强更
    }

    public boolean isMustUpdate()
    {
        if(type == Type.MUSTUPDATE)
        {
            return true;
        }
        return false;
    }
}
