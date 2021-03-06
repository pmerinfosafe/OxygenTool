package com.oxygen.util;
/**
 * @program: qxygenTool-通用
 * @description: 业务异常
 * @author: pmer_infoSafe
 * @create: 2019-11-14 14:46
 **/
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    String errorCode = "10000";
    String errorMessage = null;

    public BusinessException(String errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String errorMessage, Throwable e) {
        super(e);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
