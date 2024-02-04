package com.jiuxiao.mini.jdbc.callback;

import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Description 数据库连接回调接口
 * @Author 悟道九霄
 * @Date 2024/2/2 11:28
 */
@FunctionalInterface
public interface ConnectCallback<T> {

    @Nullable
    T doInConnection(Connection connection) throws SQLException;
}
