package com.jiuxiao.mini.jdbc.util;

import com.jiuxiao.mini.jdbc.base.DataSourceTransactionManager;
import jakarta.annotation.Nullable;

import java.sql.Connection;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 14:42
 * @Description 事务工具类
 */
public class TransactionUtil {

    @Nullable
    public static Connection getCurrentConnection() {
        TransactionStatus ts = DataSourceTransactionManager.transactionStatus.get();
        return ts == null ? null : ts.connection;
    }
}
