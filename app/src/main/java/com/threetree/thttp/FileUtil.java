package com.threetree.thttp;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/7/18.
 */

public class FileUtil {
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "HttpTest";
    public static final String NAME  = "temp_img";

    // 展开文件夹
    private static String getDictionaryPath(String path)
    {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getFilePath()
    {
        return getDictionaryPath(ROOT_PATH + File.separator + NAME);
    }
}
