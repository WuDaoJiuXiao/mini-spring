package com.jiuxiao.mini.jdbc.callback;

import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:30
 * @Description PreparedStatement 回调接口
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {

    @Nullable
    T doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException;
}
