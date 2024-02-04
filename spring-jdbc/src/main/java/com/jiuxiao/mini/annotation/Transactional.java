package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Author 悟道九霄
 * @Date 2024/2/2 14:47
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

    String value() default "platformTransactionManager";
}
