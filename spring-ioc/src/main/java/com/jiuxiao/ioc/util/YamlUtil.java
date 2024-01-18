package com.jiuxiao.ioc.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 悟道九霄
 * @Date 2024/1/18 15:04
 * @Description yaml读取工具类
 */
public class YamlUtil {

    /**
     * @param path yaml 文件路径
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @description 加载 yaml 文件
     * @date 2024/1/18 15:11
     */
    public static Map<String, Object> loadYaml(String path) {
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        NoImplicitResolver implicitResolver = new NoImplicitResolver();
        Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, implicitResolver);
        return ClassPathUtil.readInputStream(path, yaml::load);
    }

    /**
     * @param path yaml 文件路径
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @description 加载 yaml 文件，以平面集合的方式
     * @date 2024/1/18 15:15
     */
    public static Map<String, Object> loadYamlAsPlainMap(String path) {
        Map<String, Object> loadedYaml = loadYaml(path);
        LinkedHashMap<String, Object> plainMap = new LinkedHashMap<>();
        convertToPlain(loadedYaml, "", plainMap);
        return plainMap;
    }

    /**
     * @param source 元数据集合
     * @param prefix 前缀
     * @param plain  转换后的数据集合
     * @return: void
     * @description 将 yaml 多层属性拍平为 properties 型一维属性
     * @date 2024/1/18 15:06
     */
    private static void convertToPlain(Map<String, Object> source, String prefix, Map<String, Object> plain) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> children = (Map<String, Object>) value;
                convertToPlain(children, prefix + key + ".", plain);
            } else if (value instanceof List) {
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }
}
