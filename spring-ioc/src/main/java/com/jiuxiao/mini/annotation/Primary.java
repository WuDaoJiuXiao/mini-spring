package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description @Autowired 注解
 * @Author 悟道九霄
 * @Date 2024/1/19 14:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Primary {
}
