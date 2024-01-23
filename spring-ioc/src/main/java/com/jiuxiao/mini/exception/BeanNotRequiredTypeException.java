package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:31
 * @Description Bean不是要求的类型时的异常类
 */
public class BeanNotRequiredTypeException extends BeansException{

    public BeanNotRequiredTypeException() {
    }

    public BeanNotRequiredTypeException(String message) {
        super(message);
    }

    public BeanNotRequiredTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanNotRequiredTypeException(Throwable cause) {
        super(cause);
    }
}
