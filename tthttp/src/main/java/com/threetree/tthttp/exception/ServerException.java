package com.threetree.tthttp.exception;

/**
 * Created by Administrator on 2018/3/28.
 * 服务器返回的异常
 */

public class ServerException extends RuntimeException {
    private int code;
    private String msg;
    public ServerException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
