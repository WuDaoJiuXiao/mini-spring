package com.jiuxiao.mini.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 16:40
 * @Description
 */
public abstract class BeforeInvocationHandlerAdepter implements InvocationHandler {

    public abstract void before(Object proxy, Method method, Object[] args);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(proxy, method, args);
        return method.invoke(proxy, args);
    }
}
