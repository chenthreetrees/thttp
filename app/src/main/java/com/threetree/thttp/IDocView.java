package com.threetree.thttp;

import com.threetree.tthttp.viewbind.ILoadingView;

/**
 * Created by Administrator on 2018/7/30.
 */

public interface IDocView extends ILoadingView {
    void onSuccess(String doc);
}
