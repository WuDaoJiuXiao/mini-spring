package com.jiuxiao.mini.ioc;

import com.jiuxiao.mini.annotation.Dog;
import com.jiuxiao.mini.io.PropertyResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

public class ApplicationContextUtilTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRequiredApplicationContext() throws Exception {
        PropertyResolver propertyResolver = new PropertyResolver(new Properties());
        Class<Dog> dogClass = Dog.class;
        AnnoConfigApplicationContext context = new AnnoConfigApplicationContext(dogClass, propertyResolver);
        ApplicationContextUtil.setApplicationContext(context);

        ApplicationContext result = ApplicationContextUtil.getRequiredApplicationContext();
        Assert.assertEquals(AnnoConfigApplicationContext.class, result.getClass());
    }
}