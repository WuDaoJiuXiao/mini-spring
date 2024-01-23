package com.jiuxiao.mini.ioc;

import com.jiuxiao.mini.exception.BeanCreationException;
import jakarta.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author 悟道九霄
 * @Date 2024/1/19 14:43
 * @Description Bean定义类
 */
public class BeanDefinition implements Comparable<BeanDefinition> {

    /* 全局唯一的 Bean 名称 */
    private final String name;

    /* Bean的对象 */
    private final Class<?> beanClass;

    /* Bean的实例 */
    private Object instance = null;

    /* Bean的构造方法 */
    private final Constructor<?> constructor;

    /* 生成Bean的工厂方法名称 */
    private final String factoryName;

    /* 生成Bean的工厂方法 */
    private final Method factoryMethod;

    /* 加载优先级 */
    private final int order;

    /* 是否被 @Primary 注解标注 */
    private final boolean primary;

    /* 是否初始化 */
    private boolean init = false;

    /* 初始化该 Bean 的方法名称 */
    private String initMethodName;

    /* 销毁该 Bean 的方法名称 */
    private String destroyMethodName;

    /* 初始化该 Bean 的方法 */
    private Method initMethod;

    /* 销毁该 Bean 的方法 */
    private Method destroyMethod;

    public BeanDefinition(String name, Class<?> beanClass, Constructor<?> constructor, int order, boolean primary, String initMethodName,
                          String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.factoryName = null;
        this.factoryMethod = null;
        this.order = order;
        this.primary = primary;
        constructor.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, initMethod, destroyMethodName, destroyMethod);
    }

    public BeanDefinition(String name, Class<?> beanClass, String factoryName, Method factoryMethod, int order, boolean primary, String initMethodName,
                          String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = null;
        this.factoryName = factoryName;
        this.factoryMethod = factoryMethod;
        this.order = order;
        this.primary = primary;
        factoryMethod.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, initMethod, destroyMethodName, destroyMethod);
    }

    /**
     * @param initMethodName    初始化方法名
     * @param initMethod        初始化方法
     * @param destroyMethodName 销毁方法名
     * @param destroyMethod     销毁方法
     * @return: void
     * @description 设置Bean的初始化方法和销毁方法
     * @date 2024/1/19 14:52
     */
    private void setInitAndDestroyMethod(String initMethodName, Method initMethod, String destroyMethodName, Method destroyMethod) {
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        if (initMethod != null) {
            initMethod.setAccessible(true);
        }
        if (destroyMethod != null) {
            destroyMethod.setAccessible(true);
        }
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Nullable
    public Object getInstance() {
        return instance;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Nullable
    public String getFactoryName() {
        return factoryName;
    }

    @Nullable
    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isInit() {
        return init;
    }

    @Nullable
    public String getInitMethodName() {
        return initMethodName;
    }

    @Nullable
    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    @Nullable
    public Method getInitMethod() {
        return initMethod;
    }

    @Nullable
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public Object getRequiredInstance() {
        if (this.instance == null) {
            throw new BeanCreationException(String.format(
                    "Instance of bean with name '%s' and type '%s' is not instantiated during current stage.",
                    this.getName(), this.getBeanClass().getName()));
        }
        return this.instance;
    }

    public void setInstance(Object instance) {
        Objects.requireNonNull(instance, "Bean instance is null.");
        if (!this.beanClass.isAssignableFrom(instance.getClass())) {
            throw new BeanCreationException(String.format(
                    "Instance '%s' of Bean '%s' is not the expected type: %s",
                    instance, instance.getClass().getName(), this.beanClass.getName()));
        }
        this.instance = instance;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    @Override
    public String toString() {
        return "BeanDefinition = {" +
                "name = " + name + ", " +
                "beanClass = " + beanClass.getName() + ", " +
                "factory = " + getCreateDetail() + ", " +
                "initMethod = " + (initMethod == null ? "null" : initMethod.getName()) + ", " +
                "destroyMethod = " + (destroyMethod == null ? "null" : destroyMethod.getName()) + ", " +
                "primary = " + primary + ", " +
                "instance = " + instance + "}";
    }

    /**
     * @return: java.lang.String
     * @description 获得Bean创建的详细信息
     * @date 2024/1/19 15:09
     */
    private String getCreateDetail() {
        if (this.factoryMethod != null) {
            Class<?>[] parameterTypes = this.factoryMethod.getParameterTypes();
            String[] paramArray = Arrays.stream(parameterTypes).map(Class::getSimpleName).toArray(String[]::new);
            String params = String.join(", ", paramArray);
            String simpleName = this.factoryMethod.getDeclaringClass().getSimpleName();
            return simpleName + "." + this.factoryMethod.getName() + "(" + params + ")";
        }
        return null;
    }

    @Override
    public int compareTo(BeanDefinition definition) {
        int cmp = Integer.compare(this.order, definition.order);
        if (cmp != 0) {
            return cmp;
        }
        return this.name.compareTo(definition.name);
    }
}
