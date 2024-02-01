package com.jiuxiao.mini.ioc;

import com.jiuxiao.mini.BeanScanApplication;
import com.jiuxiao.mini.custom.CustomBean;
import com.jiuxiao.mini.io.PropertyResolver;
import com.jiuxiao.mini.ioc.convert.ConvertorBean;
import com.jiuxiao.mini.ioc.destroy.DestroyBean;
import com.jiuxiao.mini.ioc.destroy.SpecifyDestroy;
import com.jiuxiao.mini.ioc.imported.DateConfiguration;
import com.jiuxiao.mini.ioc.imported.TimeConfiguration;
import com.jiuxiao.mini.ioc.init.InitBean;
import com.jiuxiao.mini.ioc.init.SpecifyInit;
import com.jiuxiao.mini.ioc.nested.OuterNestedBean;
import com.jiuxiao.mini.ioc.proxy.EdenBean;
import com.jiuxiao.mini.ioc.proxy.EveProxyBean;
import com.jiuxiao.mini.ioc.proxy.InjectProxyBeanByConstructor;
import com.jiuxiao.mini.ioc.proxy.InjectProxyBeanByProperty;
import com.jiuxiao.mini.ioc.primary.Cafe;
import com.jiuxiao.mini.ioc.primary.CatBean;
import com.jiuxiao.mini.ioc.primary.MochaCafe;
import com.jiuxiao.mini.ioc.sub.SubBean;
import com.jiuxiao.mini.ioc.sub.inner.SubInnerBean;
import com.jiuxiao.mini.util.YamlUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.Map;
import java.util.Properties;

public class AnnoConfigApplicationContextTest {

    @Mock
    PropertyResolver propertyResolver;

    @InjectMocks
    AnnoConfigApplicationContext applicationContext;

    @Before
    public void setUp() {
        String contextPath = "./application.yaml";
        Properties properties = new Properties();
        Map<String, Object> plainMap = YamlUtil.loadYamlAsPlainMap(contextPath);
        for (String key : plainMap.keySet()) {
            properties.put(key, plainMap.get(key));
        }
        propertyResolver = new PropertyResolver(properties);
        applicationContext = new AnnoConfigApplicationContext(BeanScanApplication.class, propertyResolver);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testContainsBean() {
        Assert.assertTrue(applicationContext.containsBean("customBean"));
        Assert.assertTrue(applicationContext.containsBean("mathTeacher"));
    }

    @Test
    public void testCustomAnnotation() {
        CustomBean customBean = applicationContext.getBean(CustomBean.class);
        Object bean = applicationContext.getBean("customBean");
        Assert.assertNotNull(customBean);
        Assert.assertNotNull(bean);
    }

    @Test
    public void testPostConstruct() {
        InitBean initBean = applicationContext.getBean(InitBean.class);
        SpecifyInit specifyInit = applicationContext.getBean(SpecifyInit.class);
        Assert.assertNotNull(initBean);
        Assert.assertNotNull(specifyInit);
        Assert.assertEquals(initBean.frameworkName, "Mini@Spring");
        Assert.assertEquals(specifyInit.frameworkName, "Mini@Spring");
    }

    @Test
    public void testImport() {
        DateConfiguration dateBean = applicationContext.getBean(DateConfiguration.class);
        TimeConfiguration timeBean = applicationContext.getBean(TimeConfiguration.class);
        Object nowDate = applicationContext.getBean("nowDate");
        Object nowLocalTime = applicationContext.getBean("nowLocalTime");
        Object nowLocalDateTime = applicationContext.getBean("nowLocalDateTime");
        Assert.assertNotNull(dateBean);
        Assert.assertNotNull(timeBean);
        Assert.assertNotNull(nowDate);
        Assert.assertNotNull(nowLocalTime);
        Assert.assertNotNull(nowLocalDateTime);
    }

    @Test
    public void testPerDestroy() {
        DestroyBean destroyBean = applicationContext.getBean(DestroyBean.class);
        SpecifyDestroy specifyDestroy = applicationContext.getBean(SpecifyDestroy.class);
        Assert.assertEquals(destroyBean.frameworkLanguage, "java");
        Assert.assertEquals(specifyDestroy.frameworkLanguage, "java");

        applicationContext.close();
        Assert.assertNull(destroyBean.frameworkLanguage);
        Assert.assertNull(specifyDestroy.frameworkLanguage);
    }

    @Test
    public void testNestedForClass() {
        OuterNestedBean outerBean = applicationContext.getBean(OuterNestedBean.class);
        OuterNestedBean.NestedBean nestedBean = applicationContext.getBean(OuterNestedBean.NestedBean.class);
        OuterNestedBean.NestedBean.InnerNestedBean innerBean = applicationContext.getBean(OuterNestedBean.NestedBean.InnerNestedBean.class);
        Assert.assertNotNull(outerBean);
        Assert.assertNotNull(nestedBean);
        Assert.assertNotNull(innerBean);
    }

    @Test
    public void testSubNestedForPackage() {
        SubBean subBean = applicationContext.getBean(SubBean.class);
        SubInnerBean innerBean = applicationContext.getBean(SubInnerBean.class);
        Assert.assertNotNull(subBean);
        Assert.assertNotNull(innerBean);
    }

    @Test
    public void testPrimary() {
        // 当某个类型的 bean 存在多个实例时，使用 @Primary 注解标注的实例，会在 @Autowired 时被优先加载
        Cafe cafeBean = applicationContext.getBean(Cafe.class);
        CatBean catBean = applicationContext.getBean(CatBean.class);
        Assert.assertNotNull(cafeBean);
        Assert.assertNotNull(catBean);
        Assert.assertEquals(MochaCafe.class, cafeBean.getClass());
        Assert.assertEquals("Tom", catBean.name);
    }

    @Test
    public void testProxy() {
        // 代理类创建 bean 对象
        EdenBean edenBean = applicationContext.getBean(EdenBean.class);
        Assert.assertSame(EveProxyBean.class, edenBean.getClass());
        Assert.assertEquals("8888", edenBean.getServerPort());
        Assert.assertEquals("Asia", edenBean.getServerArea());

        // 确保代理的字段没有被注入
        Assert.assertNull(edenBean.serverPort);
        Assert.assertNull(edenBean.serverArea);

        // 其他的 bean 通过代理对象注入实例
        InjectProxyBeanByProperty byProperty = applicationContext.getBean(InjectProxyBeanByProperty.class);
        InjectProxyBeanByConstructor byConstructor = applicationContext.getBean(InjectProxyBeanByConstructor.class);
        Assert.assertSame(edenBean, byProperty.edenBean);
        Assert.assertSame(edenBean, byConstructor.edenBean);
    }

    @Test
    public void testConvertor() {
        ConvertorBean bean = applicationContext.getBean(ConvertorBean.class);

        Assert.assertNotNull(bean.convertByteBoxed);
        Assert.assertEquals((byte) -121, bean.convertByte);
        Assert.assertEquals(new Byte("-121"), bean.convertByteBoxed);

        Assert.assertNotNull(bean.convertShortBoxed);
        Assert.assertEquals((short) 485, bean.convertShort);
        Assert.assertEquals(new Short("485"), bean.convertShortBoxed);

        Assert.assertNotNull(bean.convertIntBoxed);
        Assert.assertEquals(789456, bean.convertInt);
        Assert.assertEquals(new Integer("789456"), bean.convertIntBoxed);

        Assert.assertNotNull(bean.convertLongBoxed);
        Assert.assertEquals(78_0000_0000L, bean.convertLong);
        Assert.assertEquals(new Long("7800000000"), bean.convertLongBoxed);

        Assert.assertNotNull(bean.convertFloatBoxed);
        Assert.assertEquals(0.458, bean.convertFloat, 0.0001F);
        Assert.assertEquals(new Float("0.458"), bean.convertFloat, 0.0001F);

        Assert.assertNotNull(bean.convertDoubleBoxed);
        Assert.assertEquals(-78.5421, bean.convertDouble, 0.0000001D);
        Assert.assertEquals(new Double("-78.5421"), bean.convertDoubleBoxed, 0.0000001D);

        Assert.assertNotNull(bean.convertBooleanBoxed);
        Assert.assertEquals(Boolean.FALSE, bean.convertBoolean);
        Assert.assertEquals(Boolean.FALSE, bean.convertBooleanBoxed);

        Assert.assertNotNull(bean.convertCharBoxed);
        Assert.assertEquals('m', bean.convertChar);
        Assert.assertEquals(Character.valueOf('m'), bean.convertCharBoxed);

        Assert.assertNotNull(bean.convertString);
        Assert.assertEquals("hello", bean.convertString);

        Assert.assertEquals(LocalDate.parse("2024-01-22"), bean.convertLocalDate);
        Assert.assertEquals(LocalTime.parse("23:08:07"), bean.convertLocalTime);
        Assert.assertEquals(LocalDateTime.parse("2024-01-22T23:08:07"), bean.convertLocalDateTime);
        Assert.assertEquals(ZonedDateTime.parse("2024-01-22T23:08:07+08:00[Asia/Shanghai]"), bean.convertZonedDateTime);
        Assert.assertEquals(Duration.parse("P2DT3H4M"), bean.convertDuration);
        Assert.assertEquals(ZoneId.of("Asia/Shanghai"), bean.convertZoneId);
    }
}