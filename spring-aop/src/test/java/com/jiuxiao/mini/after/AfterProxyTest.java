package com.jiuxiao.mini.after;


import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.AnnoConfigApplicationContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

public class AfterProxyTest {

    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        propertyResolver = new PropertyResolver(properties);
        applicationContext = new AnnoConfigApplicationContext(AfterApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAfterProxy() {
        AniBean proxy = applicationContext.getBean(AniBean.class);
        Assert.assertEquals("This cat is named Jerry", proxy.animalName("Tom"));
    }
}
