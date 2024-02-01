package com.jiuxiao.mini.around;

import com.jiuxiao.mini.anno.NamedCat;
import com.jiuxiao.mini.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:26
 * @Description 环绕通知拦截器
 */
@Component
public class AroundInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        NamedCat namedCat = method.getAnnotation(NamedCat.class);
        // 拦截标记有 @NamedCat 注解的方法，为动物起个名字，处理后返回
        if (namedCat != null) {
            String returnResult = (String) method.invoke(proxy, args);
            if (returnResult.contains("no named")) {
                returnResult = returnResult.replace("no named", "named Tom");
            }
            return returnResult;
        }
        return method.invoke(proxy, args);
    }
}
