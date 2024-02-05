package com.jiuxiao.mini;

import com.jiuxiao.mini.annotation.ComponentScan;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.annotation.Import;
import com.jiuxiao.mini.jdbc.config.JdbcConfiguration;

/**
 * @Author 悟道九霄
 * @Date 2024/2/5 15:06
 * @Description
 */
@ComponentScan
@Configuration
@Import({JdbcConfiguration.class})
public class JdbcApplication {
}
