package com.jiuxiao.mini.ioc.destroy;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Value;
import jakarta.annotation.PreDestroy;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 14:06
 * @Description
 */
@Component
public class DestroyBean {

    @Value("${mini.framework.language}")
    public String frameworkLanguage;

    @PreDestroy
    void destroy() {
        this.frameworkLanguage = null;
    }
}
