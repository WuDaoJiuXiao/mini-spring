package com.jiuxiao.mini.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 14:02
 * @Description 代理对象解析器
 */
public class ProxyResolver {

    private final Logger logger = LoggerFactory.getLogger(ProxyResolver.class);

    /* 使用该第三方库动态生成字节码，代替 CGLIB */
    private final ByteBuddy byteBuddy = new ByteBuddy();

    /* 代理对象实例 */
    private static ProxyResolver proxyResolver = null;

    public ProxyResolver() {
    }

    /**
     * @return: com.jiuxiao.mini.aop.ProxyResolver
     * @description 获取代理解析器的对象实例
     * @date 2024/2/1 14:42
     */
    public static ProxyResolver getInstance() {
        if (proxyResolver == null) {
            proxyResolver = new ProxyResolver();
        }
        return proxyResolver;
    }

    /**
     * @param bean    要创建代理的 bean
     * @param handler 方法调用处理器
     * @return: T
     * @description 创建代理对象
     * @date 2024/2/1 14:45
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T bean, InvocationHandler handler) {
        Class<?> clazz = bean.getClass();
        logger.debug("Create proxy for bean {} @{}", clazz.getName(), Integer.toHexString(bean.hashCode()));
        Class<?> proxyClass = this.byteBuddy
                // 子类用默认使用无参数构造方法
                .subclass(clazz, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                // 拦截所有的 public 方法
                .method(ElementMatchers.isPublic())
                // 拦截之后将方法调用代理到原始的 bean 上
                .intercept(InvocationHandlerAdapter.of((proxy, method, args) -> handler.invoke(bean, method, args)))
                .make()
                .load(clazz.getClassLoader())
                .getLoaded();
        Object proxyObject;
        try {
            proxyObject = proxyClass.getConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return ((T) proxyObject);
    }
}
