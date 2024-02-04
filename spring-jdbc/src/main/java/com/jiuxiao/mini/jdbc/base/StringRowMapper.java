package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.jdbc.callback.RowMapper;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:35
 * @Description 字符串字段 Mapper 层接口回调类
 */
public class StringRowMapper implements RowMapper<String> {

    static final StringRowMapper stringRowMapper = new StringRowMapper();

    @Nullable
    @Override
    public String mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return resultSet.getString(1);
    }
}
