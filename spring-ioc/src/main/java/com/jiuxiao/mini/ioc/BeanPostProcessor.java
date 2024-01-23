package com.jiuxiao.mini.ioc;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 15:46
 * @Description Bean后处理器
 */
public interface BeanPostProcessor {

    /* 在创建完成一个新的 Bean 之后调用 */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /* 在 bean.init() 方法被调用之后调用 */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    /* 在 bean.setXXX() 方法被调用之前调用 */
    default Object postProcessOnSetProperty(Object bean, String beanName) {
        return bean;
    }
}
