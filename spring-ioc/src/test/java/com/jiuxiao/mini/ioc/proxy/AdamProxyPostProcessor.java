package com.jiuxiao.mini.ioc.proxy;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Order;
import com.jiuxiao.mini.ioc.BeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author 悟道九霄
 * @Date 2024/1/31 15:58
 * @Description
 */
@Order(100)
@Component
public class AdamProxyPostProcessor implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(AdamProxyPostProcessor.class);

    Map<String, Object> edenBeanMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (EdenBean.class.isAssignableFrom(bean.getClass())) {
            logger.debug("Create Adam. proxy {} for bean {}", beanName, bean);
            AdamProxyBean adamProxyBean = new AdamProxyBean(((EdenBean) bean));
            this.edenBeanMap.put(beanName, bean);
            return adamProxyBean;
        }
        return bean;
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object targetBean = edenBeanMap.get(beanName);
        if (targetBean != null) {
            logger.debug("Auto set property for {} from adamProxy {} to origin bean {}",
                    beanName, bean, targetBean);
            return targetBean;
        }
        return bean;
    }
}
