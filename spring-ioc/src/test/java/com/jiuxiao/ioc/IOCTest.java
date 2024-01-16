package com.jiuxiao.ioc;

import com.jiuxiao.ioc.reslover.ResourceResolver;
import org.junit.Test;

import java.util.List;

/**
 * @Author 悟道九霄
 * @Date 2024/1/16 11:17
 * @Description ioc模块测试类
 */
public class IOCTest {

    @Test
    public void testResourceResolverForFindClass() throws Exception {
        String packageName = "com.jiuxiao";
        ResourceResolver resolver = new ResourceResolver(packageName);
        List<String> classList = resolver.findClass();
        classList.forEach(System.out::println);
    }
}
