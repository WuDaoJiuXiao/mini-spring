package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:28
 * @Description Bean有关的异常类
 */
public class BeansException extends MiniRuntimeException{

    public BeansException() {
    }

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }
}
