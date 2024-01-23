package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:37
 * @Description 不是单一的Bean时的异常类
 */
public class NoUniqueBeanDefinitionException extends BeanDefinitionException{

    public NoUniqueBeanDefinitionException() {
    }

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }

    public NoUniqueBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoUniqueBeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
