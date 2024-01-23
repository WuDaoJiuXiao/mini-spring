package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:26
 * @Description 框架自定义运行时的异常类
 */
public class MiniRuntimeException extends RuntimeException{

    public MiniRuntimeException() {

    }

    public MiniRuntimeException(String message) {
        super(message);
    }

    public MiniRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MiniRuntimeException(Throwable cause) {
        super(cause);
    }
}
