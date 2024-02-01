package com.jiuxiao.mini.ioc.proxy;

import com.jiuxiao.mini.annotation.Autowired;
import com.jiuxiao.mini.annotation.Component;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 16:10
 * @Description
 */
@Component
public class InjectProxyBeanByProperty {

    @Autowired
    public EdenBean edenBean;
}
