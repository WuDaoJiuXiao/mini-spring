package com.jiuxiao.mini.jdbc.callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:24
 * @Description PreparedStatement 建造器接口
 */
@FunctionalInterface
public interface PreparedStatementCreator {

    PreparedStatement createPreparedStatement(Connection connection) throws SQLException;
}
