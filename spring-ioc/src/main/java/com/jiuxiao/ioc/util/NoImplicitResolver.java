package com.jiuxiao.ioc.util;

import org.yaml.snakeyaml.resolver.Resolver;

/**
 * @Description 禁用 yaml 库中默认的所有隐式转换，全部解析为字符串
 * @Author 悟道九霄
 * @Date 2024/1/18 15:13
 */
public class NoImplicitResolver extends Resolver {

    public NoImplicitResolver() {
        super();
        super.yamlImplicitResolvers.clear();
    }
}
