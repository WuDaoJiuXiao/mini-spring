package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:29
 * @Description Bean创建时的异常类
 */
public class BeanCreationException extends BeansException{

    public BeanCreationException() {
    }

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreationException(Throwable cause) {
        super(cause);
    }
}
