package com.jiuxiao.mini.ioc.init;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Value;
import jakarta.annotation.PostConstruct;


/**
 * @Author 悟道九霄
 * @Date 2024/1/30 16:45
 * @Description
 */
@Component
public class InitBean {

    @Value("${server.name.prefix}")
    private String prefixName;

    @Value("${server.name.suffix}")
    private String suffixName;

    public String frameworkName;

    @PostConstruct
    void init() {
        this.frameworkName = this.prefixName + "@" + this.suffixName;
    }
}
