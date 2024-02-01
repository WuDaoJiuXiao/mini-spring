package com.jiuxiao.mini.exception;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 14:58
 * @Description AOP配置异常
 */
public class AopConfigException extends MiniRuntimeException{

    public AopConfigException() {
        super();
    }

    public AopConfigException(String message) {
        super(message);
    }

    public AopConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopConfigException(Throwable cause) {
        super(cause);
    }
}
