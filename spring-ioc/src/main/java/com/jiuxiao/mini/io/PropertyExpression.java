package com.jiuxiao.mini.io;

import java.util.Objects;

/**
 * @Author 悟道九霄
 * @Date 2024/1/17 15:45
 * @Description 属性表达式类
 */
public class PropertyExpression {

    private String key;

    private String defaultValue;

    public PropertyExpression(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "PropertyExpression{" +
                "key='" + key + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyExpression that = (PropertyExpression) o;
        return Objects.equals(key, that.key) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, defaultValue);
    }
}
