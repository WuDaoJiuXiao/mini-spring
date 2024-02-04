package com.jiuxiao.mini.jdbc.util;

import java.sql.Connection;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 14:43
 * @Description 事务状态类
 */
public class TransactionStatus {

    final Connection connection;

    public TransactionStatus(Connection connection) {
        this.connection = connection;
    }
}
