package com.vc.onlinepay.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * @描述: 异常定义
 * @作者:nada
 * @时间:2018/12/21
 **/
public class OnlineDbException extends NestableRuntimeException {

    private static final long serialVersionUID = -654893533794556357L;

    public OnlineDbException (String errorCode) {
        super (errorCode);
    }

    public OnlineDbException (String errorCode, Throwable cause) {
        super (errorCode, cause);
    }

    public OnlineDbException (String errorCode, String errorDesc) {
        super (errorCode + ":" + errorDesc);
    }

    public OnlineDbException (String errorCode, String errorDesc, Throwable cause) {
        super (errorCode + ":" + errorDesc, cause);
    }

    public OnlineDbException (Throwable cause) {
        super (cause);
    }

}
