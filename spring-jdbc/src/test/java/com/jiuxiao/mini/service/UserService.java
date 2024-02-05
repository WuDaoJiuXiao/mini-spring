package com.jiuxiao.mini.service;

import com.jiuxiao.mini.annotation.Autowired;
import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Transactional;
import com.jiuxiao.mini.config.JdbcBaseConfig;
import com.jiuxiao.mini.entry.User;
import com.jiuxiao.mini.exception.DataAccessException;
import com.jiuxiao.mini.jdbc.base.JdbcTemplate;

import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/5 14:43
 * @Description
 */
@Component
@Transactional
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User createUser(String name, int age) throws SQLException {
        Number number = jdbcTemplate.updateAndReturnGenerateKey(JdbcBaseConfig.INSERT_USER, name, age);
        User user = new User();
        user.setId(number.intValue());
        user.setName(name);
        user.setAge(age);
        return user;
    }

    public User selectUserById(int id) {
        try {
            return jdbcTemplate.queryObject(JdbcBaseConfig.SELECT_USER_BY_ID, User.class, id);
        } catch (Exception e) {
            throw new DataAccessException("There is not have the data for id = " + id, e);
        }
    }

    public String selectUserNameById(int id) {
        User user = selectUserById(id);
        String name = "";
        if (user != null) {
            name = user.getName();
        }
        return name;
    }

    public void updateUser(User user) throws SQLException {
        jdbcTemplate.update(JdbcBaseConfig.UPDATE_USER, user.getName(), user.getAge(), user.getId());
    }

    public void deleteUser(User user) throws SQLException {
        jdbcTemplate.update(JdbcBaseConfig.DELETE_USER_BY_ID, user.getId());
    }
}
