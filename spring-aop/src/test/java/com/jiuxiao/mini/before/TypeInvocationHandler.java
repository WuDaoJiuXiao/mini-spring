package com.jiuxiao.mini.before;


import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.aop.BeforeInvocationHandlerAdepter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Component
public class TypeInvocationHandler extends BeforeInvocationHandlerAdepter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void before(Object proxy, Method method, Object[] args) {
        logger.info("[Before Info] : [This animal is cat]");
    }
}
