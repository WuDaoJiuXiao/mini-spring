package com.jiuxiao.mini.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 16:40
 * @Description
 */
public abstract class AfterInvocationHandlerAdepter implements InvocationHandler {

    public abstract Object after(Object proxy, Object returnValue, Method method, Object[] args);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o = method.invoke(proxy, args);
        return after(proxy, o, method, args);
    }
}
