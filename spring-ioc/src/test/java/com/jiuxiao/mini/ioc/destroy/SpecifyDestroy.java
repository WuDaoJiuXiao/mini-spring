package com.jiuxiao.mini.ioc.destroy;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 14:06
 * @Description
 */
public class SpecifyDestroy {

    public String frameworkLanguage;

    SpecifyDestroy(String frameworkLanguage) {
        this.frameworkLanguage = frameworkLanguage;
    }

    void destroy() {
        this.frameworkLanguage = null;
    }
}
