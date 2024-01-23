package com.jiuxiao.mini.util;

import org.junit.Test;

import java.util.Map;

public class YamlUtilTest {

    @Test
    public void testLoadYaml() throws Exception {
        Map<String, Object> result = YamlUtil.loadYaml("./application.yaml");
        for (String key : result.keySet()) {
            System.out.println(key + " : " + result.get(key));
        }
    }

    @Test
    public void testLoadYamlAsPlainMap() throws Exception {
        Map<String, Object> result = YamlUtil.loadYamlAsPlainMap("./application.yaml");
        for (String key : result.keySet()) {
            System.out.println(key + " : " + result.get(key));
        }
    }
}