package com.jiuxiao.mini.ioc.nested;

import com.jiuxiao.mini.annotation.Component;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:12
 * @Description
 */
@Component
public class OuterNestedBean {

    @Component
    public static class NestedBean {

        @Component
        public static class InnerNestedBean {

        }
    }
}
