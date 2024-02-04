package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.exception.DataAccessException;
import com.jiuxiao.mini.jdbc.callback.ConnectCallback;
import com.jiuxiao.mini.jdbc.callback.PreparedStatementCallback;
import com.jiuxiao.mini.jdbc.callback.PreparedStatementCreator;
import com.jiuxiao.mini.jdbc.callback.RowMapper;
import com.jiuxiao.mini.jdbc.util.TransactionUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 11:45
 * @Description Jdbc模板类
 */
public class JdbcTemplate {

    final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param sql  要执行的 SQL 语句
     * @param args 携带的参数
     * @return: java.lang.Number
     * @description 查询数值对象
     * @date 2024/2/2 14:23
     */
    public Number queryNumber(String sql, Object... args) throws DataAccessException {
        return queryObject(sql, NumberRowMapper.numberRowMapper, args);
    }

    /**
     * @param sql   要执行的 SQL 语句
     * @param clazz 查询结果要转换的对象
     * @param args  携带的参数
     * @return: T
     * @description 查询指定对象
     * @date 2024/2/2 14:26
     */
    @SuppressWarnings("unchecked")
    public <T> T queryObject(String sql, Class<T> clazz, Object... args) throws DataAccessException {
        if (clazz == String.class) {
            return ((T) queryObject(sql, StringRowMapper.stringRowMapper, args));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) queryObject(sql, BooleanRowMapper.booleanRowMapper, args);
        }
        if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
            return (T) queryObject(sql, NumberRowMapper.numberRowMapper, args);
        }
        return queryObject(sql, new BeanRowMapper<>(clazz), args);
    }

    /**
     * @param sql       要执行的 SQL 语句
     * @param rowMapper 通用的 Mapper 对象
     * @param args      携带的参数
     * @return: T
     * @description 查询指定对象
     * @date 2024/2/2 14:33
     */
    public <T> T queryObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), (PreparedStatement preparedStatement) -> {
            T res = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (res == null) {
                        res = rowMapper.mapRow(resultSet, resultSet.getRow());
                    } else {
                        throw new DataAccessException("Multiple rows found");
                    }
                }
            }
            if (res == null) {
                throw new DataAccessException("Empty result set");
            }
            return res;
        });
    }

    /**
     * @param sql   要执行的 SQL 语句
     * @param clazz 查询结果要转换的对象
     * @param args  携带的参数
     * @return: T
     * @description 查询指定集合
     * @date 2024/2/2 14:40
     */
    public <T> List<T> queryList(String sql, Class<T> clazz, Object... args) throws DataAccessException {
        return queryList(sql, new BeanRowMapper<>(clazz), args);
    }

    /**
     * @param sql       要执行的 SQL 语句
     * @param rowMapper 通用的 Mapper 对象
     * @param args      携带的参数
     * @return: java.util.List<T>
     * @description 查询指定集合
     * @date 2024/2/2 15:54
     */
    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), (PreparedStatement preparedStatement) -> {
            ArrayList<T> list = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(rowMapper.mapRow(resultSet, resultSet.getRow()));
                }
            }
            return list;
        });
    }

    /**
     * @param sql  执行的 sql 语句
     * @param args 携带的参数
     * @return: java.lang.Number
     * @description 更新并返回参数 key
     * @date 2024/2/2 15:50
     */
    public Number updateAndReturnGenerateKey(String sql, Object... args) throws DataAccessException {
        return execute(
                (Connection connection) -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    bindArgs(preparedStatement, args);
                    return preparedStatement;
                },
                (PreparedStatement preparedStatement) -> {
                    int update = preparedStatement.executeUpdate();
                    if (update == 0) {
                        throw new DataAccessException("0 rows inserted");
                    }
                    if (update > 1) {
                        throw new DataAccessException("Multiple rows inserted");
                    }
                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        while (resultSet.next()) {
                            return ((Number) resultSet.getObject(1));
                        }
                    }
                    throw new DataAccessException("Should not reach here");
                }
        );
    }

    /**
     * @param sql  要执行的 sql 语句
     * @param args 携带的参数
     * @return: int
     * @description 更新操作
     * @date 2024/2/2 15:42
     */
    public int update(String sql, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), PreparedStatement::executeUpdate);
    }

    /**
     * @param preparedStatement 预处理语句
     * @param callback          回调函数
     * @return: T
     * @description 执行 sql 语句
     * @date 2024/2/2 15:40
     */
    public <T> T execute(PreparedStatementCreator preparedStatement, PreparedStatementCallback<T> callback) {
        return execute((Connection connect) -> {
            try (PreparedStatement prepared = preparedStatement.createPreparedStatement(connect)) {
                return callback.doInPreparedStatement(prepared);
            }
        });
    }

    /**
     * @param connectCallback 连接回调函数
     * @return: T
     * @description 执行 sql 语句
     * @date 2024/2/2 15:30
     */
    public <T> T execute(ConnectCallback<T> connectCallback) throws DataAccessException {
        Connection currentConnection = TransactionUtil.getCurrentConnection();
        if (currentConnection != null) {
            try {
                return connectCallback.doInConnection(currentConnection);
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        }
        // 获取新连接
        Connection connection;
        try {
            connection = dataSource.getConnection();
            final boolean autoCommit = connection.getAutoCommit();
            if (!autoCommit) {
                connection.setAutoCommit(true);
            }
            T result = connectCallback.doInConnection(connection);
            if (!autoCommit) {
                connection.setAutoCommit(false);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * @param sql  要执行的 sql 语句
     * @param args 携带的参数
     * @return: com.jiuxiao.mini.jdbc.callback.PreparedStatementCreator
     * @description 预处理语句建造器
     * @date 2024/2/2 15:24
     */
    private PreparedStatementCreator preparedStatementCreator(String sql, Object... args) {
        return (Connection connection) -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            bindArgs(preparedStatement, args);
            return preparedStatement;
        };
    }

    /**
     * @param preparedStatement 预处理语句
     * @param args              携带的参数
     * @return: void
     * @description 将参数绑定到预处理语句上
     * @date 2024/2/2 15:27
     */
    private void bindArgs(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            preparedStatement.setObject(i + 1, args[i]);
        }
    }
}
