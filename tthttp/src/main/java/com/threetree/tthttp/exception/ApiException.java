package com.threetree.tthttp.exception;

/**
 * Created by Administrator on 2018/3/28.
 * 自定义异常类，对于网络操作异常的统一处理
 */

public class ApiException extends Exception {
    private int code;
    private String displayMessage;
    private boolean isServiceException;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public void setServiceException(boolean isServiceException)
    {
        this.isServiceException = isServiceException;
    }

    public boolean isServiceException()
    {
        return isServiceException;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public int getCode() {
        return code;
    }
}
