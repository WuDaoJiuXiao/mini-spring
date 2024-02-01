package com.jiuxiao.mini.after;


import com.jiuxiao.mini.annotation.Around;
import com.jiuxiao.mini.annotation.Component;

@Component
@Around("namedInvocationHandler")
public class AniBean {

    public String animalName(String animalType) {
        return "This cat is named " + animalType;
    }
}
