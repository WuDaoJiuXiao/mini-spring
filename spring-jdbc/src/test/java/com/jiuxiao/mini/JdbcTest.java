package com.jiuxiao.mini;

import com.jiuxiao.mini.config.JdbcBaseConfig;
import com.jiuxiao.mini.entry.User;
import com.jiuxiao.mini.exception.DataAccessException;
import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.AnnoConfigApplicationContext;
import com.jiuxiao.mini.jdbc.base.JdbcTemplate;
import com.jiuxiao.mini.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @Author 悟道九霄
 * @Date 2024/2/5 15:07
 * @Description
 */
public class JdbcTest {

    private final Logger logger = LoggerFactory.getLogger(JdbcTest.class);

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        JdbcBaseConfig.cleanDatabase();
        PropertyResolver propertyResolver = JdbcBaseConfig.createPropertyResolver();
        applicationContext = new AnnoConfigApplicationContext(JdbcApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testJdbcBaseOperator() throws SQLException {
        JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        jdbcTemplate.update(JdbcBaseConfig.CREATE_USER);

        // 测试代理
        UserService userService = applicationContext.getBean(UserService.class);
        Assert.assertNotSame(UserService.class, userService.getClass());

        // 测试创建表及新增字段
        User tom = userService.createUser("Tom", 18);
        Assert.assertNotNull(tom);
        Assert.assertEquals(1, tom.getId());
        Assert.assertEquals("Tom", tom.getName());
        Assert.assertEquals(18, tom.getAge());

        // 添加批量测试数据
        String[] nameList = {"Jerry", "Jack", "Marry", "FanYe", "Tina"};
        int[] ageList = {20, 23, 9, 26, 13};
        for (int i = 0; i < nameList.length; i++) {
            User user = userService.createUser(nameList[i], ageList[i]);
            Assert.assertNotNull(user);
            logger.info("Add user = " + user + " success");
        }

        // 测试查询所有用户列表
        logger.info("初始化数据库数据...");
        for (int i = 1; i <= 6; i++) {
            User user = userService.selectUserById(i);
            Assert.assertNotNull(user);
            logger.info("Select user = " + user);
        }

        // 测试根据 ID 查询用户
        User userJack = userService.selectUserById(3);
        Assert.assertEquals("Jack", userJack.getName());
        Assert.assertEquals(23, userJack.getAge());

        // 测试根据 ID 查询用户名
        String username = userService.selectUserNameById(5);
        Assert.assertEquals("FanYe", username);

        // 测试更新用户
        userJack.setAge(88);
        userService.updateUser(userJack);
        Assert.assertEquals(88, userJack.getAge());
        logger.info("更新用户之后...");
        for (int i = 1; i <= 6; i++) {
            User user = userService.selectUserById(i);
            Assert.assertNotNull(user);
            logger.info("Select user = " + user);
        }

        // 测试删除用户
        userService.deleteUser(userJack);
        logger.info("删除用户之后...");
        Assert.assertThrows(DataAccessException.class, () -> {
            userService.selectUserById(3);
        });
        for (int i = 1; i <= 6; i++) {
            if (i != 3) {
                User user = userService.selectUserById(i);
                Assert.assertNotNull(user);
                logger.info("Select user = " + user);
            }
        }
    }
}
