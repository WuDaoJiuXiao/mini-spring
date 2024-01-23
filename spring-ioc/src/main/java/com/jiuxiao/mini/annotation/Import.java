package com.jiuxiao.mini.annotation;

import java.lang.annotation.*;

/**
 * @Description @Autowired 注解
 * @Author 悟道九霄
 * @Date 2024/1/19 14:16
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Import {

    Class<?>[] value();
}
