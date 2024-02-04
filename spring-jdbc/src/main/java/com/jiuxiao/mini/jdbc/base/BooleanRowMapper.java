package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.jdbc.callback.RowMapper;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:35
 * @Description 布尔类型字段 Mapper 层接口回调类
 */
public class BooleanRowMapper implements RowMapper<Boolean> {

    static final BooleanRowMapper booleanRowMapper = new BooleanRowMapper();

    @Nullable
    @Override
    public Boolean mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return resultSet.getBoolean(1);
    }
}
