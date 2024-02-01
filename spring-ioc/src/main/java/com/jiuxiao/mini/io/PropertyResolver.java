package com.jiuxiao.mini.io;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 * @Author 悟道九霄
 * @Date 2024/1/16 16:02
 * @Description 属性解析器，模拟 @Value 注解与 ${} 表达式取值
 */
public class PropertyResolver {

    private final HashMap<String, String> propertiesMap = new HashMap<>();

    private final HashMap<Class<?>, Function<String, Object>> convertMap = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);

    public PropertyResolver(Properties properties) {
        propertiesMap.putAll(System.getenv());
        Set<String> propertyNames = properties.stringPropertyNames();
        propertyNames.forEach(name -> {
            propertiesMap.put(name, properties.getProperty(name));
            logger.debug("Set properties in map of {{}:{}}", name, properties.getProperty(name));
        });
        registryConverts();
    }

    /**
     * @param key 属性的 key
     * @return: boolean
     * @description 是否包含某属性
     * @date 2024/1/17 17:35
     */
    public boolean containsProperty(String key) {
        return propertiesMap.containsKey(key);
    }

    /**
     * @param key 属性的 key
     * @return: java.lang.String
     * @description 只使用 key 获取属性
     * @date 2024/1/17 17:06
     */
    @Nullable
    public String getProperty(String key) {
        PropertyExpression parsed = parsePropertyExpression(key);
        if (parsed != null) {
            // 带默认值的查询
            if (parsed.getDefaultValue() != null) {
                return getProperty(parsed.getKey(), parsed.getDefaultValue());
            } else {// 不带默认值的查询
                return getRequiredProperty(parsed.getKey());
            }
        }
        // 普通值
        return parseValue(propertiesMap.get(key));
    }

    /**
     * @param key          属性的 key
     * @param defaultValue 属性默认值
     * @return: java.lang.String
     * @description 使用 key 和默认值获取属性
     * @date 2024/1/18 14:25
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? parseValue(defaultValue) : value;
    }

    /**
     * @param key    属性的 key
     * @param target 要转换的目标
     * @return: T
     * @description 将获取的对象转换为对应的目标对象，不带默认值
     * @date 2024/1/18 14:29
     */
    @Nullable
    public <T> T getProperty(String key, Class<T> target) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return convert(target, value);
    }

    /**
     * @param key          属性的 key
     * @param target       要转换的目标
     * @param defaultValue 默认值
     * @return: T
     * @description 将获取的对象转换为对应的目标对象，带默认值
     * @date 2024/1/18 14:30
     */
    public <T> T getProperty(String key, Class<T> target, T defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return convert(target, key);
    }

    /**
     * @param key 属性的 key
     * @return: java.lang.String
     * @description 不带默认值的查询
     * @date 2024/1/18 14:12
     */
    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    /**
     * @param key    属性的 key
     * @param target 默认值
     * @return: T
     * @description 带默认值的查询
     * @date 2024/1/18 14:12
     */
    public <T> T getRequiredProperty(String key, Class<T> target) {
        T value = getProperty(key, target);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    /**
     * @return: void
     * @description 注册常用类型的函数映射器
     * @date 2024/1/17 17:23
     */
    private void registryConverts() {
        convertMap.put(byte.class, Byte::parseByte);
        convertMap.put(Byte.class, Byte::valueOf);
        convertMap.put(short.class, Short::parseShort);
        convertMap.put(Short.class, Short::valueOf);
        convertMap.put(int.class, Integer::parseInt);
        convertMap.put(Integer.class, Integer::valueOf);
        convertMap.put(long.class, Long::parseLong);
        convertMap.put(Long.class, Long::valueOf);
        convertMap.put(float.class, Float::parseFloat);
        convertMap.put(Float.class, Float::valueOf);
        convertMap.put(double.class, Double::parseDouble);
        convertMap.put(Double.class, Double::valueOf);
        convertMap.put(char.class, s -> s.charAt(0));
        convertMap.put(Character.class, s -> s.charAt(0));
        convertMap.put(String.class, lambda -> lambda);
        convertMap.put(boolean.class, Boolean::parseBoolean);
        convertMap.put(Boolean.class, Boolean::valueOf);
        convertMap.put(LocalDate.class, LocalDate::parse);
        convertMap.put(LocalTime.class, LocalTime::parse);
        convertMap.put(LocalDateTime.class, LocalDateTime::parse);
        convertMap.put(ZonedDateTime.class, ZonedDateTime::parse);
        convertMap.put(Duration.class, Duration::parse);
        convertMap.put(ZoneId.class, ZoneId::of);
    }

    /**
     * @param expression 表达式
     * @return: com.jiuxiao.mini.io.PropertyExpression
     * @description 解析字符串为表达式对象
     * @date 2024/1/17 17:07
     */
    private PropertyExpression parsePropertyExpression(String expression) {
        if (expression == null || expression.isEmpty()) return null;
        if (expression.startsWith("${") && expression.endsWith("}")) {
            int index = expression.indexOf(":");
            if (index == -1) {
                String expKey = expression.substring(2, expression.length() - 1);
                return new PropertyExpression(expKey, null);
            } else {
                String expKey = expression.substring(2, index);
                return new PropertyExpression(expKey, expression.substring(index + 1, expression.length() - 1));
            }
        }
        return null;
    }

    /**
     * @param clazz 类对象
     * @param value 要转换的值
     * @return: T
     * @description 将字符串转为指定的对象
     * @date 2024/1/17 17:15
     */
    @SuppressWarnings("unchecked")
    private <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> function = convertMap.get(clazz);
        if (function == null) {
            throw new IllegalArgumentException("Unsupported value type : " + clazz.getName());
        }
        return (T) function.apply(value);
    }

    /**
     * @param value 要解析的值
     * @return: java.lang.String
     * @description 解析正常的表达式
     * @date 2024/1/18 14:20
     */
    private String parseValue(String value) {
        PropertyExpression expression = parsePropertyExpression(value);
        if (expression == null) {
            return value;
        }
        if (expression.getDefaultValue() != null) {
            return getProperty(expression.getKey(), expression.getDefaultValue());
        } else {
            return getRequiredProperty(expression.getKey());
        }
    }
}
