package com.jiuxiao.mini.around;

import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.AnnoConfigApplicationContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:11
 * @Description
 */
public class AroundProxyTest {

    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.put("animal", "cat");
        propertyResolver = new PropertyResolver(properties);
        applicationContext = new AnnoConfigApplicationContext(AroundApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAround() {
        AnimalBean animalBean = applicationContext.getBean(AnimalBean.class);
        System.out.println("animalBean.getClass().getName() = " + animalBean.getClass().getName());

        Assert.assertNotSame(AnimalBean.class, animalBean.getClass());
        // 此时代理对象的属性没有被注入
        Assert.assertNull(animalBean.animalType);

        // 拦截器拦截到使用 @NamedCat 标注的方法，使用 AroundInvocationHandler 操作该方法的返回结果，实现 AOP
        Assert.assertEquals("This animal is cat, named Tom", animalBean.animalName());
        // 没有使用 @NamedCat 标注的方法则不会进行任何操作
        Assert.assertEquals("This animal is cat", animalBean.animalInfo());

        // 使用 @Autowired 注入值
        BiologyBean biologyBean = applicationContext.getBean(BiologyBean.class);
        Assert.assertNotNull(biologyBean.animalBean);
        Assert.assertSame(animalBean, biologyBean.animalBean);
        Assert.assertEquals("This animal is cat, named Tom", biologyBean.animalBean.animalName());
    }
}
