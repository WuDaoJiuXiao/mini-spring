package com.jiuxiao.ioc;

import com.jiuxiao.ioc.io.PropertyResolver;
import com.jiuxiao.ioc.io.Resource;
import com.jiuxiao.ioc.io.ResourceResolver;
import com.jiuxiao.ioc.util.YamlUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author 悟道九霄
 * @Date 2024/1/16 11:17
 * @Description ioc模块测试类
 */
public class IOCTest {

    /**
     * @return: void
     * @description 测试 ResourceResolver 的查找 class 功能
     * @date 2024/1/18 15:23
     */
    @Test
    public void testResourceResolverForFindClass() {
        String packageName = "com.jiuxiao";
        boolean scanJar = true;
        ResourceResolver resolver = new ResourceResolver(packageName);
        List<Resource> classList = resolver.findClass(resource -> {
            String name = resource.getName();
            String path = resource.getPath();
            if (name != null && path != null) {
                return resource;
            }
            return null;
        }, scanJar);
        classList.forEach(System.out::println);
    }

    /**
     * @return: void
     * @description 测试 yaml 工具类加载 yaml 配置文件的功能
     * @date 2024/1/18 15:24
     */
    @Test
    public void testYamlUtilForLoadYaml() {
        String yaml = "/application.yaml";
        Map<String, Object> yamlMap = YamlUtil.loadYaml(yaml);
        for (String key : yamlMap.keySet()) {
            System.out.println(key + " : " + yamlMap.get(key));
        }

        Map<String, Object> plainMap = YamlUtil.loadYamlAsPlainMap(yaml);
        for (String key : plainMap.keySet()) {
            System.out.println(key + " : " + plainMap.get(key));
        }
    }

    /**
     * @return: void
     * @description 测试 PropertyResolver 基本功能
     * @date 2024/1/18 15:39
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testPropertyResolverFor() {
        Properties properties = new Properties();
        properties.put("example.os.type", "windows");
        properties.put("example.int", "99999");
        properties.put("example.long", "49489498465465");
        properties.put("example.double", "5.21841");
        properties.put("example.string", "Spring");
        properties.put("example.localDatetime", "2023-03-29T21:45:01");
        properties.put("example.localTime", "23:59:59");
        PropertyResolver resolver = new PropertyResolver(properties);

        System.out.println(resolver.containsProperty("name"));
        System.out.println(resolver.getProperty("example.int", Integer.class));
        System.out.println(resolver.getProperty("${example.long}", Long.class));
        System.out.println(resolver.getProperty("${example.double}", Double.class));
        System.out.println(resolver.getProperty("example.string"));
        System.out.println(resolver.getProperty("example.localDatetime", LocalDateTime.class));
        System.out.println(resolver.getProperty("${JAVA_HOME}"));
        System.out.println(resolver.getRequiredProperty("${M2_HOME}", String.class));
    }
}
