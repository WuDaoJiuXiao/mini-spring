package com.jiuxiao.mini.before;


import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.AnnoConfigApplicationContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

public class BeforeProxyTest {

    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        propertyResolver = new PropertyResolver(properties);
        applicationContext = new AnnoConfigApplicationContext(BeforeApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBeforeProxy() {
        CatBean catBean = applicationContext.getBean(CatBean.class);
        Assert.assertEquals("This animal named Tom", catBean.showAnimalName("Tom"));
    }
}
