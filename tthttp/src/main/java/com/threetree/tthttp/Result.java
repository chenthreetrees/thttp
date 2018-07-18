package com.threetree.tthttp;

/**
 * Created by Administrator on 2018/3/28.
 * 网络请求结构
 */
public class Result<T> {
    public boolean success;
    public String errorMessage;
    public int errorCode;
    public T data;
}
