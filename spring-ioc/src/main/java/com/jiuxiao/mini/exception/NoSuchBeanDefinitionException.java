package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:34
 * @Description 没有此类的Bean定义时的异常类
 */
public class NoSuchBeanDefinitionException extends BeanDefinitionException{

    public NoSuchBeanDefinitionException() {
    }

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }

    public NoSuchBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
