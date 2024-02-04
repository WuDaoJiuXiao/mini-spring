package com.jiuxiao.mini.jdbc.callback;

import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:22
 * @Description 行操作接口
 */
@FunctionalInterface
public interface RowMapper<T> {

    @Nullable
    T mapRow(ResultSet resultSet, int rowNumber) throws SQLException;
}
