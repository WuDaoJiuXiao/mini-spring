package com.jiuxiao.mini.ioc.init;

import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.annotation.Value;

/**
 * @Author 悟道九霄
 * @Date 2024/1/30 16:51
 * @Description
 */
@Configuration
public class SpecifyInitConfig {

    @Bean(initMethod = "init")
    public SpecifyInit createSpecifyInitBean(@Value("${server.name.prefix}") String prefix,
                                             @Value("${server.name.suffix}") String suffix) {
        return new SpecifyInit(prefix, suffix);
    }
}
