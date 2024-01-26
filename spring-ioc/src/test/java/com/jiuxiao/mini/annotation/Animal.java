package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Author 悟道九霄
 * @Date 2024/1/26 15:26
 */
@Base
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Animal {

    String value() default "";

    String name() default "";
}
