package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:30
 * @Description Bean定义时的异常类
 */
public class BeanDefinitionException extends BeansException{

    public BeanDefinitionException() {
    }

    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
