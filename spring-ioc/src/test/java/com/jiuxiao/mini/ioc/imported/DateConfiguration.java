package com.jiuxiao.mini.ioc.imported;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;

import java.util.Date;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 10:26
 * @Description
 */
@Configuration
public class DateConfiguration {

    @Bean
    public Date nowDate() {
        return new Date();
    }
}
