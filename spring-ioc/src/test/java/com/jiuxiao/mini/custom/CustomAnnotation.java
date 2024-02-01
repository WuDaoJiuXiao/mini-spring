package com.jiuxiao.mini.custom;

import com.jiuxiao.mini.annotation.Component;

import java.lang.annotation.*;

/**
 * @Description
 * @Author 悟道九霄
 * @Date 2024/1/28 16:39
 */
@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface CustomAnnotation {

    String value() default "";
}
