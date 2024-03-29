package com.jiuxiao.mini.jdbc.config;

import com.jiuxiao.mini.annotation.Autowired;
import com.jiuxiao.mini.annotation.Bean;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.annotation.Value;
import com.jiuxiao.mini.jdbc.base.DataSourceTransactionManager;
import com.jiuxiao.mini.jdbc.base.JdbcTemplate;
import com.jiuxiao.mini.jdbc.base.TransactionalBeanPostProcessor;
import com.jiuxiao.mini.jdbc.callback.PlatformTransactionManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 14:51
 * @Description Jdbc配置类
 */
@Configuration
public class JdbcConfiguration {

    @Bean(destroyMethod = "close")
    DataSource dataSource(@Value("${mini.datasource.url}") String url,
                          @Value("${mini.datasource.username}") String username,
                          @Value("${mini.datasource.password}") String password,
                          @Value("${mini.datasource.driver-class-name:}") String driver,
                          @Value("${mini.datasource.maximum-pool-size}") int maximumPoolSize,
                          @Value("${mini.datasource.minimum-pool-size}") int minimumPoolSize,
                          @Value("${mini.datasource.connection-timeout}") int connTimeout) {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(false);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        if (driver != null) {
            config.setDriverClassName(driver);
        }
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumPoolSize);
        config.setConnectionTimeout(connTimeout);
        return new HikariDataSource(config);
    }

    @Bean
    JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    TransactionalBeanPostProcessor transactionalBeanPostProcessor() {
        return new TransactionalBeanPostProcessor();
    }

    @Bean
    PlatformTransactionManager platformTransactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
