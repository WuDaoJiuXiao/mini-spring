package com.jiuxiao.mini.exception;

import com.jiuxiao.mini.exception.MiniRuntimeException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:16
 * @Description
 */
public class TransactionException extends MiniRuntimeException {

    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
