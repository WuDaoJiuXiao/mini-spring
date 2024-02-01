package com.jiuxiao.mini.ioc.proxy;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Value;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:50
 * @Description
 */
@Component
public class EdenBean {

    @Value("${server.port}")
    public String serverPort;

    public String serverArea;

    @Value("${server.area}")
    public void setServerArea(String serverArea) {
        this.serverArea = serverArea;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getServerArea() {
        return this.serverArea;
    }
}
