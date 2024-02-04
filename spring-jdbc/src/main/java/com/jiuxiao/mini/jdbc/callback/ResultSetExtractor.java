package com.jiuxiao.mini.jdbc.callback;

import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:27
 * @Description 结果集执行器接口
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {

    @Nullable
    T extractData(ResultSet resultSet) throws SQLException;
}
