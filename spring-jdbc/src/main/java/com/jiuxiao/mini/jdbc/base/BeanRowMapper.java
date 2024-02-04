package com.jiuxiao.mini.jdbc.base;

import com.jiuxiao.mini.exception.DataAccessException;
import com.jiuxiao.mini.jdbc.callback.RowMapper;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 悟道九霄
 * @Date 2024/2/2 14:29
 * @Description Bean 到 Row 的映射器
 */
public class BeanRowMapper<T> implements RowMapper<T> {

    final Logger logger = LoggerFactory.getLogger(BeanRowMapper.class);

    Class<T> clazz;

    Constructor<T> constructor;

    Map<String, Field> fieldMap = new HashMap<>();

    Map<String, Method> methodMap = new HashMap<>();

    public BeanRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        try {
            this.constructor = clazz.getConstructor();
        } catch (ReflectiveOperationException e) {
            throw new DataAccessException(String.format(
                    "No public default constructor found for class %s when build BeanRowMapper", clazz.getName()
            ), e);
        }
        for (Field field : clazz.getFields()) {
            String name = field.getName();
            this.fieldMap.put(name, field);
            logger.debug("Add row mapping {} to filed {}", name, field.getName());
        }
        for (Method method : clazz.getMethods()) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 1) {
                String name = method.getName();
                if (name.length() >= 4 && name.startsWith("set")) {
                    String properties = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    this.methodMap.put(properties, method);
                    logger.debug("Add row mapping {} to {}({})", properties, name, parameters[0].getType().getSimpleName());
                }
            }
        }
    }

    @Nullable
    @Override
    public T mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        T bean;
        try {
            bean = this.constructor.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                Method method = this.methodMap.get(columnLabel);
                if (method != null) {
                    method.invoke(bean, resultSet.getObject(columnLabel));
                } else {
                    Field field = this.fieldMap.get(columnLabel);
                    if (field != null) {
                        field.set(bean, resultSet.getObject(columnLabel));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new DataAccessException(String.format(
                    "Could not mapping result set to class %s", this.clazz.getName()
            ), e);
        }
        return bean;
    }
}
