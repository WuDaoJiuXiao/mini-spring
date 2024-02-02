package com.jiuxiao.mini.secret;

import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.AnnoConfigApplicationContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

public class SecretProxyTest {

    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        propertyResolver = new PropertyResolver(properties);
        applicationContext = new AnnoConfigApplicationContext(SecretApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMetricProxy() {
        BaseWorker baseWorker = applicationContext.getBean(BaseWorker.class);
        Assert.assertNotNull(baseWorker);

        String origin = "Spring";
        String md5Str = "0x38008dd81c2f4d7985ecf6e0ce8af1d1";
        Assert.assertEquals(md5Str, baseWorker.secretStr(origin));

        SecretInvocationHandler secretHandler = applicationContext.getBean(SecretInvocationHandler.class);
        Assert.assertEquals("md5", secretHandler.secretMap.get("secretStr"));
    }
}
