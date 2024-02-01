package com.jiuxiao.mini.after;


import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.aop.AfterInvocationHandlerAdepter;

import java.lang.reflect.Method;

@Component
public class NamedInvocationHandler extends AfterInvocationHandlerAdepter {

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args) {
        if (returnValue instanceof String) {
            String returnResult = (String) returnValue;
            // 将名称由 Tom 改为 Jerry
            if (returnResult.contains("Tom")) {
                returnResult = returnResult.replace("Tom", "Jerry");
            }
            return returnResult;
        }
        return returnValue;
    }
}
