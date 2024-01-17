package com.jiuxiao.ioc;

import com.jiuxiao.ioc.io.Resource;
import com.jiuxiao.ioc.io.ResourceResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @Author 悟道九霄
 * @Date 2024/1/16 11:17
 * @Description ioc模块测试类
 */
public class IOCTest {

    @Test
    public void testResourceResolverForFindClass() {
        String packageName = "com.jiuxiao";
        boolean scanJar = true;
        ResourceResolver resolver = new ResourceResolver(packageName);
        List<Resource> classList = resolver.findClass(resource -> {
            String name = resource.getName();
            String path = resource.getPath();
            if (name != null && path != null){
                return resource;
            }
            return null;
        }, scanJar);
        classList.forEach(System.out::println);
    }
}
