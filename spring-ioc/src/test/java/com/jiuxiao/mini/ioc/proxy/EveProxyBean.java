package com.jiuxiao.mini.ioc.proxy;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:55
 * @Description
 */
public class EveProxyBean extends EdenBean{

    final EdenBean edenBean;

    public EveProxyBean(EdenBean edenBean) {
        this.edenBean = edenBean;
    }

    @Override
    public void setServerArea(String serverArea) {
        this.edenBean.setServerArea(serverArea);
    }

    @Override
    public String getServerPort() {
        return this.edenBean.getServerPort();
    }

    @Override
    public String getServerArea() {
        return this.edenBean.getServerArea();
    }
}
