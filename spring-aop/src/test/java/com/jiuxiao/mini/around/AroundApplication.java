package com.jiuxiao.mini.around;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.ComponentScan;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.aop.AroundProxyPostProcessor;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:15
 * @Description
 */
@Configuration
@ComponentScan
public class AroundApplication {

    @Bean
    public AroundProxyPostProcessor createAroundProxyPostProcessor() {
        return new AroundProxyPostProcessor();
    }
}
