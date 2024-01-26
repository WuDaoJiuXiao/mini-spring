package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description 测试用的最基本注解类
 * @Author 悟道九霄
 * @Date 2024/1/26 15:22
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Base {

    String value() default "";
}
