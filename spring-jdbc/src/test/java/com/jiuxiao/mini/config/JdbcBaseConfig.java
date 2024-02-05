package com.jiuxiao.mini.config;

import com.jiuxiao.mini.io.PropertyResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @Author 悟道九霄
 * @Date 2024/2/5 14:07
 * @Description
 */
public class JdbcBaseConfig {

    public static final String CREATE_USER = "create table user (" +
            "id integer primary key autoincrement, " +
            "name varchar(128) not null, " +
            "age integer" +
            ")";

    public static final String INSERT_USER = "insert into user (name, age) values (?, ?)";
    public static final String UPDATE_USER = "update user set name = ?, age = ? where id = ?";
    public static final String DELETE_USER_BY_ID = "delete from user where id = ?";
    public static final String SELECT_USER_BY_ID = "select * from user where id = ?";

    public static PropertyResolver createPropertyResolver() {
        Properties properties = new Properties();
        properties.put("mini.datasource.url", "jdbc:sqlite:test.db");
        properties.put("mini.datasource.username", "sa");
        properties.put("mini.datasource.password", "");
        properties.put("mini.datasource.driver-class-name", "org.sqlite.JDBC");
        properties.put("mini.datasource.maximum-pool-size", "5");
        properties.put("mini.datasource.minimum-pool-size", "1");
        properties.put("mini.datasource.connection-timeout", "30000");
        return new PropertyResolver(properties);
    }

    public static void cleanDatabase() {
        Path dbPath = Paths.get("test.db").normalize().toAbsolutePath();
        try {
            Files.deleteIfExists(dbPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete database", e);
        }
    }
}
