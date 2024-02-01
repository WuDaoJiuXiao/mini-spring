package com.jiuxiao.mini.aop;

import com.jiuxiao.mini.exception.AopConfigException;
import com.jiuxiao.mini.exception.BeansException;
import com.jiuxiao.mini.ioc.ApplicationContextUtil;
import com.jiuxiao.mini.ioc.BeanDefinition;
import com.jiuxiao.mini.ioc.BeanPostProcessor;
import com.jiuxiao.mini.ioc.ConfigApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:43
 * @Description
 */
public abstract class AnnoProxyPostProcessor<A extends Annotation> implements BeanPostProcessor {

    /* 标注有指定注解的所有 bean 存储集合 */
    Map<String, Object> beanMap = new HashMap<>();

    /* 要拦截的注解 class 对象 */
    private final Class<A> annoClazz;

    public AnnoProxyPostProcessor() {
        this.annoClazz = getParameterizedType();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        // 检查是否存在 class 级别的注解
        A annotation = clazz.getAnnotation(annoClazz);
        if (annotation != null) {
            String handlerName;
            try {
                handlerName = ((String) annotation.annotationType().getMethod("value").invoke(annotation));
            } catch (ReflectiveOperationException e) {
                throw new AopConfigException(String.format(
                        "@%s must have value() returned string type", this.annoClazz.getSimpleName()
                ));
            }
            Object proxyObject = createProxy(bean, handlerName);
            beanMap.put(beanName, bean);
            return proxyObject;
        } else {
            return bean;
        }
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object o = this.beanMap.get(beanName);
        return o != null ? o : bean;
    }

    /**
     * @param bean        代理的 bean 对象
     * @param handlerName 处理器名称
     * @return: java.lang.Object
     * @description 创建代理
     * @date 2024/2/1 15:55
     */
    private Object createProxy(Object bean, String handlerName) {
        ConfigApplicationContext context = (ConfigApplicationContext) ApplicationContextUtil.getRequiredApplicationContext();
        BeanDefinition beanDefinition = context.findBeanDefinition(handlerName);
        if (beanDefinition == null) {
            throw new AopConfigException(String.format("@%s proxy handler %s not found", annoClazz.getSimpleName(), handlerName));
        }
        Object handler = beanDefinition.getInstance();
        if (handler == null) {
            handler = context.createBeanAsEarlySingleton(beanDefinition);
        }
        if (handler instanceof InvocationHandler) {
            InvocationHandler invocationHandler = (InvocationHandler) handler;
            return ProxyResolver.getInstance().createProxy(bean, invocationHandler);
        } else {
            throw new AopConfigException(String.format(
                    "@%s proxy handler %s is not type of %s", annoClazz.getSimpleName(), handlerName, InvocationHandler.class.getName()
            ));
        }
    }

    /**
     * @return: java.lang.Class<A>
     * @description 获取代理对象的餐宿类型
     * @date 2024/2/1 16:06
     */
    @SuppressWarnings("unchecked")
    private Class<A> getParameterizedType() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + "does not have parameterized type");
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] arguments = parameterizedType.getActualTypeArguments();
        if (arguments.length != 1) {
            throw new IllegalArgumentException("Class " + getClass().getName() + "has more than 1 parameterized types");
        }
        Type argument = arguments[0];
        if (!(argument instanceof Class<?>)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + "does not have parameterized type of class");
        }
        return ((Class<A>) argument);
    }
}
