package com.jiuxiao.mini.ioc.primary;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.annotation.Primary;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:32
 * @Description
 */
@Configuration
public class PrimaryBeanConfig {

    @Bean
    @Primary
    public CatBean createCatTom() {
        return new CatBean("Tom");
    }

    @Bean
    public CatBean createCatGarfield() {
        return new CatBean("Garfield");
    }
}
