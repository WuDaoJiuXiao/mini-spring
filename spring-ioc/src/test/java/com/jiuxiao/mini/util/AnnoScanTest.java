package com.jiuxiao.mini.util;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Configuration;
import com.jiuxiao.mini.custom.CustomAnnotation;
import com.jiuxiao.mini.util.anno.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author 悟道九霄
 * @Date 2024/1/30 15:42
 * @Description 注解扫描测试类
 */
public class AnnoScanTest {

    @Test
    public void testIsNotComponent() {
        Assert.assertNull(ClassUtil.findAllAnnotation(OriginBean.class, Component.class));
    }

    @Test
    public void testWithComponent() {
        Component component = ClassUtil.findAllAnnotation(OriginComponentBean.class, Component.class);
        Assert.assertNotNull(component);
        Assert.assertEquals(component.annotationType(), Component.class);
        Assert.assertEquals(ClassUtil.getBeanName(OriginComponentBean.class), "originComponentBean");
    }

    @Test
    public void testComponentWithName() {
        Component component = ClassUtil.findAllAnnotation(OriginComponentNameBean.class, Component.class);
        Assert.assertNotNull(component);
        Assert.assertEquals(ClassUtil.getBeanName(OriginComponentNameBean.class), "originComponentNameBean");
        Assert.assertEquals(component.value(), "originComponentNameBean");
    }

    @Test
    public void testWithConfiguration() {
        Configuration configuration = ClassUtil.findAllAnnotation(OriginConfigBean.class, Configuration.class);
        Assert.assertNotNull(configuration);
        Assert.assertEquals(configuration.annotationType(), Configuration.class);
        Assert.assertEquals(ClassUtil.getBeanName(OriginConfigBean.class), "originConfigBean");
    }

    @Test
    public void testConfigurationWithName() {
        Configuration configuration = ClassUtil.findAllAnnotation(OriginConfigNameBean.class, Configuration.class);
        Assert.assertNotNull(configuration);
        Assert.assertEquals(ClassUtil.getBeanName(OriginConfigNameBean.class), "originConfigNameBean");
        Assert.assertEquals(configuration.value(), "originConfigNameBean");
    }

    @Test
    public void testCustom() {
        CustomAnnotation annotation = ClassUtil.findAllAnnotation(OriginCustomBean.class, CustomAnnotation.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.annotationType(), CustomAnnotation.class);
        Assert.assertEquals(ClassUtil.getBeanName(OriginCustomBean.class), "originCustomBean");
    }

    @Test
    public void testCustomWithName() {
        CustomAnnotation annotation = ClassUtil.findAllAnnotation(OriginCustomNameBean.class, CustomAnnotation.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.annotationType(), CustomAnnotation.class);
        Assert.assertEquals(ClassUtil.getBeanName(OriginCustomNameBean.class), "originCustomNameBean");
        Assert.assertEquals(annotation.value(), "originCustomNameBean");
    }

    @Test
    public void repetitionComponentAndConfiguration() {
        // 测试的时候需要去对应的类上解除被注释的注解
//        Assert.assertThrows(BeanDefinitionException.class, () -> {
//            ClassUtil.findAllAnnotation(RepetitionComConBean.class, Component.class);
//        });
    }

    @Test
    public void repetitionComponentAndCustom() {
        // 测试的时候需要去对应的类上解除被注释的注解
//        Assert.assertThrows(BeanDefinitionException.class, () -> {
//            ClassUtil.findAllAnnotation(RepetitionComCusBean.class, Component.class);
//        });
    }
}