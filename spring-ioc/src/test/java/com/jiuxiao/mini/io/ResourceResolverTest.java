package com.jiuxiao.mini.io;

import jdk.jfr.events.X509ValidationEvent;
import oracle.jrockit.jfr.Recording;
import org.junit.Assert;
import org.junit.Test;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

public class ResourceResolverTest {

    ResourceResolver resourceResolver = new ResourceResolver("com.jiuxiao");

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
                "com.jiuxiao.mini.scan.ScanBean",
                "com.jiuxiao.mini.scan.inner.ScanInnerBean",
                "com.jiuxiao.mini.scan.inner.sub.ScanInnerSubBean",
        };

        // 非 jar 包之下的 class
        for (String template: templates) {
            Assert.assertTrue(pathList.contains(template));
        }

        // jar 包之中的 class
        Assert.assertTrue(nameList.contains(Process.class.getSimpleName()));
        Assert.assertTrue(nameList.contains(Recording.class.getSimpleName()));
        Assert.assertTrue(nameList.contains(X509ValidationEvent.class.getSimpleName()));
        Assert.assertTrue(nameList.contains(EventHandler.class.getSimpleName()));
    }
}