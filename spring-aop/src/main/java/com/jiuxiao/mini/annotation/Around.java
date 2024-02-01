package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Author 悟道九霄
 * @Date 2024/2/1 15:21
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Around {

    String value();
}
