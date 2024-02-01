package com.jiuxiao.mini.ioc.imported;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 10:27
 * @Description
 */
@Configuration
public class TimeConfiguration {

    @Bean
    public LocalTime nowLocalTime(){
        return LocalTime.now();
    }

    @Bean
    public LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now();
    }
}
