package com.jiuxiao.mini.ioc.proxy;

/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:53
 * @Description
 */
public class AdamProxyBean extends EdenBean {

    final EdenBean edenBean;

    public AdamProxyBean(EdenBean edenBean) {
        this.edenBean = edenBean;
    }

    @Override
    public void setServerArea(String serverArea) {
        edenBean.setServerArea(serverArea);
    }

    @Override
    public String getServerPort() {
        return edenBean.getServerPort();
    }

    @Override
    public String getServerArea() {
        return edenBean.getServerArea();
    }
}
