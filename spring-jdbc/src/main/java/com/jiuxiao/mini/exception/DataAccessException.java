package com.jiuxiao.mini.exception;

import com.jiuxiao.mini.exception.MiniRuntimeException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:11
 * @Description
 */
public class DataAccessException extends MiniRuntimeException {

    public DataAccessException() {
        super();
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
