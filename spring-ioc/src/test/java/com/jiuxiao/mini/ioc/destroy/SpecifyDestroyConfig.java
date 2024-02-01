package com.jiuxiao.mini.ioc.destroy;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.annotation.Value;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 14:07
 * @Description
 */
@Configuration
public class SpecifyDestroyConfig {

    @Bean(destroyMethod = "destroy")
    public SpecifyDestroy createSpecifyDestroyBean(
            @Value("${mini.framework.language}") String frameworkLanguage) {
        return new SpecifyDestroy(frameworkLanguage);
    }
}
