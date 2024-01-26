package com.jiuxiao.mini.annotation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author 悟道九霄
 * @Date 2024/1/26 16:18
 * @Description
 */
public class NoArgsBean {

    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void destroy() {

    }

    @PreDestroy
    public void destroy(String name) {

    }
}
