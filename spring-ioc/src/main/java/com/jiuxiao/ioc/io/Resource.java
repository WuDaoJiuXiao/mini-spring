package com.jiuxiao.ioc.io;

import java.util.Objects;

/**
 * @Author 悟道九霄
 * @Date 2024/1/17 10:03
 * @Description 资源类
 */
public class Resource {

    private String name;

    private String path;

    public Resource(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(name, resource.name) && Objects.equals(path, resource.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}
