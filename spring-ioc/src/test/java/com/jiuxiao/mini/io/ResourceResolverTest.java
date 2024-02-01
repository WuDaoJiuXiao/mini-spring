package com.jiuxiao.mini.io;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ResourceResolverTest {

    ResourceResolver resourceResolver = new ResourceResolver("com.jiuxiao.mini");

    @Test
    public void testFindClass() {
        boolean scanJar = true;
        List<String> pathList = new ArrayList<>(), nameList = new ArrayList<>();
        List<Resource> clazzList = resourceResolver.findClass(resource -> resource, scanJar);
        clazzList.forEach(resource -> {
            pathList.add(resource.getPath());
            nameList.add(resource.getName());
        });
        String[] templates = {
                "file:E:\\MyCode\\project\\mini-spring\\spring-ioc\\target\\test-classes\\com\\jiuxiao\\mini\\scan\\inner\\sub\\ScanInnerSubBean.class",
                "file:E:\\MyCode\\project\\mini-spring\\spring-ioc\\target\\test-classes\\com\\jiuxiao\\mini\\scan\\inner\\ScanInnerBean.class",
                "file:E:\\MyCode\\project\\mini-spring\\spring-ioc\\target\\test-classes\\com\\jiuxiao\\mini\\scan\\ScanBean.class"
        };

        // 非 jar 包之下的 class
        for (String template: templates) {
            Assert.assertTrue(pathList.contains(template));
        }

        // jar 包之中的 class
        Assert.assertTrue(nameList.contains("jdk\\jfr\\events\\X509ValidationEvent.class"));
        Assert.assertTrue(nameList.contains("jdk\\jfr\\events\\ErrorThrownEvent.class"));
        Assert.assertTrue(nameList.contains("oracle\\jrockit\\jfr\\parser\\RandomAccessFileFLRInput.class"));
        Assert.assertTrue(nameList.contains("oracle\\jrockit\\jfr\\parser\\BufferLostEvent.class"));
        Assert.assertTrue(nameList.contains("oracle\\jrockit\\jfr\\tools\\ConCatRepository.class"));
    }
}