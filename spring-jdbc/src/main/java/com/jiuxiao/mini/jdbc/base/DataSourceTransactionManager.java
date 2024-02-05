package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.exception.DataAccessException;
import com.jiuxiao.mini.exception.TransactionException;
import com.jiuxiao.mini.jdbc.callback.PlatformTransactionManager;
import com.jiuxiao.mini.jdbc.util.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 14:44
 * @Description 数据源事务管理器
 */
public class DataSourceTransactionManager implements PlatformTransactionManager, InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(DataSourceTransactionManager.class);

    public static final ThreadLocal<TransactionStatus> transactionStatus = new ThreadLocal<>();

    final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionStatus status = transactionStatus.get();
        if (status == null) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                final boolean autoCommit = connection.getAutoCommit();
                if (autoCommit) {
                    connection.setAutoCommit(false);
                }
                try {
                    transactionStatus.set(new TransactionStatus(connection));
                    Object invoke = method.invoke(proxy, args);
                    connection.commit();
                    return invoke;
                } catch (InvocationTargetException e) {
                    logger.warn("Will rollback transaction for caused exception : {}", e.getCause() == null ? "null" : e.getCause().getClass().getName());
                    TransactionException transactionException = new TransactionException(e.getCause());
                    try {
                        connection.rollback();
                    } catch (SQLException se) {
                        transactionException.addSuppressed(se);
                    }
                    throw transactionException;
                } finally {
                    transactionStatus.remove();
                    if (autoCommit) {
                        connection.setAutoCommit(true);
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(e);
            }finally {
                transactionStatus.remove();
                assert connection != null;
                connection.close();
            }
        } else {
            return method.invoke(proxy, args);
        }
    }
}
