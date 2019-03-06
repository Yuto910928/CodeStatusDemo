package com.yuto.codestatusdemo.base.bean;

public class BaseBean<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int pCode) {
        code = pCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        message = pMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T pData) {
        data = pData;
    }
}
