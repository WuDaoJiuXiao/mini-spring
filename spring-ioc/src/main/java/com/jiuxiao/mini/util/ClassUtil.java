package com.jiuxiao.mini.util;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.exception.BeanDefinitionException;
import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 悟道九霄
 * @Date 2024/1/20 14:35
 * @Description 类加载时相关工具类
 */
public class ClassUtil {

    /**
     * @param clazz           要查找注解的类
     * @param annotationClass 要查找的注解类型
     * @return: A
     * @description 对于某个被注解标注的类，递归查找指定类型的注解，及其所有的该类型的子注解
     * @date 2024/1/20 14:48
     */
    public static <A extends Annotation> A findAllAnnotation(Class<?> clazz, Class<A> annotationClass) {
        A annotation = clazz.getAnnotation(annotationClass);
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation anno : annotations) {
            Class<? extends Annotation> annotationType = anno.annotationType();
            String packageName = getPackageName(annotationType);
            if (!packageName.equals("java.lang.annotation")) {
                A found = findAllAnnotation(annotationType, annotationClass);
                if (found != null) {
                    if (annotation != null) {
                        throw new BeanDefinitionException(
                                "Duplicate @" + annotationClass.getSimpleName() + " found on class " + clazz.getSimpleName()
                        );
                    }
                    annotation = found;
                }
            }
        }
        return annotation;
    }

    /**
     * @param annotations     所有注解对象的数组
     * @param annotationClass 要查找的注解对象
     * @return: A
     * @description 在所有注解中查找指定类型的注解
     * @date 2024/1/20 15:05
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotationClass.isInstance(annotation)) {
                return (A) (annotation);
            }
        }
        return null;
    }

    /**
     * @param method 被 @Bean 注解标注的方法名
     * @return: java.lang.String
     * @description 通过 @Bean 注解获取 bean 名称(所标注的方法名就是 bean 名称)
     * @date 2024/1/20 15:09
     */
    public static String getBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        if (bean == null) {
            return null;
        }
        String name = bean.value();
        if (name.isEmpty()) {
            name = method.getName();
        }
        return name;
    }

    /**
     * @param clazz 被 @Component 注解标注的 class 对象
     * @return: java.lang.String
     * @description 通过 @Component 注解获取 bean 名称(所标注的方法名就是 bean 名称)
     * @date 2024/1/20 15:11
     */
    public static String getBeanName(Class<?> clazz) {
        String name = "";
        Component component = clazz.getAnnotation(Component.class);
        if (component != null) {
            name = component.value();
        } else {// 类上没有标注 @Component 注解，则去其他注解中寻找 @Component 注解
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation : annotations) {
                Component findAnno = findAllAnnotation(annotation.annotationType(), Component.class);
                if (findAnno != null) {
                    try {
                        name = ((String) findAnno.annotationType().getMethod("value").invoke(findAnno));
                    } catch (ReflectiveOperationException roe) {
                        throw new BeanDefinitionException("Cannot get annotation value.", roe);
                    }
                }
            }
        }
        // 其他注解中也没有 @Component，则将类型名的首字母小写后驼峰命名结果，作为默认的 bean 名称，如 OldStudent -> oldStudent
        if (name.isEmpty()) {
            name = clazz.getSimpleName();
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    /**
     * @param clazz           被注解标识的类 class 对象
     * @param annotationClass 要查找的注解 class 对象
     * @return: java.lang.reflect.Method
     * @description 过滤出标注有指定注解、并且不含参数的方法，例如 @PostConstruct、@PreDestroy 注解的方法必须是无参的
     * @date 2024/1/20 15:26
     */
    public static Method findAnnotationMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methodList = Arrays.stream(clazz.getDeclaredMethods()).filter(
                md -> md.isAnnotationPresent(annotationClass)).peek(
                method -> {
                    if (method.getParameterCount() != 0) {
                        throw new BeanDefinitionException(String.format(
                                "Method '%s' with @%s must not have argument : '%s'",
                                method.getName(), annotationClass.getSimpleName(), clazz.getName()
                        ));
                    }
                }).collect(Collectors.toList());
        if (methodList.isEmpty()) {
            return null;
        }
        if (methodList.size() == 1) {
            return methodList.get(0);
        }
        throw new BeanDefinitionException(String.format(
                "Multiple methods with @%s found in class : '%s'", annotationClass.getSimpleName(), clazz.getName()
        ));
    }

    /**
     * @param clazz      被注解标识的类 class 对象
     * @param methodName 方法名称
     * @return: java.lang.reflect.Method
     * @description 根据方法名，过滤出标注有指定注解、并且不含参数的方法，例如 @PostConstruct、@PreDestroy 注解的方法必须是无参的
     * @date 2024/1/20 15:38
     */
    public static Method getNamedMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (ReflectiveOperationException roe) {
            throw new BeanDefinitionException(String.format(
                    "Method '%s' not found in class : '%s'", methodName, clazz.getName()
            ));
        }
    }

    /**
     * @param clazz 类对象
     * @return: java.lang.String
     * @description 获取某个类的包名，JDK 1.9 之后可以使用 getPackageName() 直接获取
     * @date 2024/1/20 14:55
     */
    private static String getPackageName(Class<?> clazz) {
        Package aPackage = clazz.getPackage();
        if (aPackage != null) {
            return aPackage.getName();
        } else {
            String clazzName = clazz.getName();
            int index = clazzName.lastIndexOf(".");
            if (index != -1) {
                return clazzName.substring(0, index);
            } else {
                return "";
            }
        }
    }
}
