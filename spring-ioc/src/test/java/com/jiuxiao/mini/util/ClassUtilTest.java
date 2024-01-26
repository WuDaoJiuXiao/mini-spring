package com.jiuxiao.mini.util;

import com.jiuxiao.mini.annotation.*;
import com.jiuxiao.mini.exception.BeanDefinitionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ClassUtilTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testFindAllAnnotation() throws Exception {
        Class<Dog> clazz = Dog.class;
        Class<Base> targetAnnotation = Base.class;
        Base base = ClassUtil.findAllAnnotation(clazz, targetAnnotation);
        Assert.assertNotNull(base);
        Assert.assertEquals(base.annotationType(), Base.class);
    }

    @Test
    public void testGetAnnotation() throws Exception {
        Annotation[] annotations = Dog.class.getAnnotations();
        CrawlAnimal crawlAnimal = ClassUtil.getAnnotation(annotations, CrawlAnimal.class);
        Assert.assertNotNull(crawlAnimal);
        Assert.assertEquals(crawlAnimal.annotationType(), CrawlAnimal.class);
    }

    @Test
    public void testGetBeanName() throws Exception {
        Method[] methods = Student.class.getMethods();
        String exceptedMethodName = "playGame";
        HashSet<String> hashSet = new HashSet<>();
        for (Method method : methods) {
            String beanName = ClassUtil.getBeanName(method);
            hashSet.add(beanName);
        }
        Assert.assertTrue(hashSet.contains(exceptedMethodName));
    }

    @Test
    public void testGetBeanName2() throws Exception {
        // 标注的是 @Component 的子注解，需要递归查找
        String mathResult = ClassUtil.getBeanName(MathTeacher.class);
        String mathName = MathTeacher.class.getSimpleName();
        String mathClassName = mathName.substring(0,1).toLowerCase() + mathName.substring(1);
        Assert.assertNotNull(mathResult);
        Assert.assertEquals(mathResult, mathClassName);

        // 直接标注 @Component
        String englishResult = ClassUtil.getBeanName(EnglishTeacher.class);
        String englishName = EnglishTeacher.class.getSimpleName();
        String englishClassName = englishName.substring(0, 1).toLowerCase() + englishName.substring(1);
        Assert.assertNotNull(englishResult);
        Assert.assertEquals(englishResult, englishClassName);
    }

    @Test
    public void testFindAnnotationMethod() throws Exception {
        Class<PostConstruct> annClazzPost = PostConstruct.class;
        Class<PreDestroy> annClazzPre = PreDestroy.class;

        // 类中不含有被 @PostConstruct 或者 @PreDestroy 这种必须无参的注解所注解的方法
        Class<Student> studentClass = Student.class;
        Method postMethod = ClassUtil.findAnnotationMethod(studentClass, annClazzPost);
        Assert.assertNull(postMethod);

        // 类中含有被 @PostConstruct 或者 @PreDestroy 这种必须无参的注解所注解的方法
        // 注解的方法是无参的，正常返回方法
        Class<NoArgsBean> noArgsBeanClass = NoArgsBean.class;
        Method postNoArgsMethod = ClassUtil.findAnnotationMethod(noArgsBeanClass, annClazzPost);
        Assert.assertNotNull(postNoArgsMethod);
        // 注解的方法是有参的，抛出异常
        exceptionRule.expect(BeanDefinitionException.class);
        ClassUtil.findAnnotationMethod(noArgsBeanClass, annClazzPre);
    }

    @Test
    public void testGetNamedMethod() throws Exception {
        String methodName = "init";
        Method method = ClassUtil.getNamedMethod(NoArgsBean.class, methodName);
        Assert.assertNotNull(method);
    }
}