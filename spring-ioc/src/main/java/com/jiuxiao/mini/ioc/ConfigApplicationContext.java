package com.jiuxiao.mini.ioc;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 15:44
 * @Description 配置应用上下文类
 */
public interface ConfigApplicationContext extends ApplicationContext {

    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(String name);

    @Nullable
    BeanDefinition findBeanDefinition(String name, Class<?> requiredType);

    Object createBeanAsEarlySingleton(BeanDefinition def);
}
