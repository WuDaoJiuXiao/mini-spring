package com.jiuxiao.mini.ioc.init;

/**
 * @Author 悟道九霄
 * @Date 2024/1/30 16:48
 * @Description
 */
public class SpecifyInit {

    private final String prefixName;

    private final String suffixName;

    public String frameworkName;

    SpecifyInit(String prefixName, String suffixName) {
        this.prefixName = prefixName;
        this.suffixName = suffixName;
    }

    void init(){
        this.frameworkName = prefixName + "@" + suffixName;
    }
}
