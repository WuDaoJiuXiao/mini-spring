package com.jiuxiao.mini.before;


import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.ComponentScan;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.aop.AroundProxyPostProcessor;

@Configuration
@ComponentScan
public class BeforeApplication {

    @Bean
    AroundProxyPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyPostProcessor();
    }
}
