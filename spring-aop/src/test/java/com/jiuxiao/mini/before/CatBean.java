package com.jiuxiao.mini.before;


import com.jiuxiao.mini.annotation.Around;
import com.jiuxiao.mini.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Around("typeInvocationHandler")
public class CatBean {

    private final Logger logger = LoggerFactory.getLogger(CatBean.class);

    public String showAnimalName(String name) {
        logger.info("This animal named " + name);
        return "This animal named " + name;
    }
}
