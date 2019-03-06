package com.yuto.codestatusdemo.base.bean;

public class CodeStatus {
    public static final int CODE_SUCCESS = 1;//请求成功
    public static final int CODE_LOADING = 0;//正在请求
    public static final int CODE_DIALOG_LOADING_SHOW = 2;//表单提交显示dialog
    public static final int CODE_DIALOG_LOADING_HIDE = 3;//表单提交返回结构隐藏dialog
    public static final int CODE_TOAST = 4;//提示

    public static final int CODE_TOKEN = 3001;//token过期
    public static final int CODE_PERMISSION = 2002;//无权限
    public static final int CODE_ERROR_HTTP = -1;//HTTP请求出错404，503
    public static final int CODE_ERROR_OTHER = -2;//其他异常json转换异常等
    /**
     * 先将常用的不需要描述的状态码封装好
     */
    public static final CodeStatus LOADING=new CodeStatus(0);
    public static final CodeStatus SUCCESS=new CodeStatus(1);
    public static final CodeStatus DIALOG_SHOW=new CodeStatus(2);
    public static final CodeStatus DIALOG_HIDE=new CodeStatus(3);
    protected int code;
    protected String message;

    public CodeStatus(int pCode) {
        code = pCode;
    }

    public CodeStatus(int pCode, String pMessage) {
        code = pCode;
        message = pMessage;
    }

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
}
