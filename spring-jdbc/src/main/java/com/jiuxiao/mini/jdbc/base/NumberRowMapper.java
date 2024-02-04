package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.jdbc.callback.RowMapper;
import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:34
 * @Description 数据字段 Mapper 层接口回调类
 */
public class NumberRowMapper implements RowMapper<Number> {

    static final NumberRowMapper numberRowMapper = new NumberRowMapper();

    @Nullable
    @Override
    public Number mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return ((Number) resultSet.getObject(1));
    }
}
