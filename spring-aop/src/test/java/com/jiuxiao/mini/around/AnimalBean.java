package com.jiuxiao.mini.around;

import com.jiuxiao.mini.anno.NamedCat;
import com.jiuxiao.mini.annotation.Around;
import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Value;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:20
 * @Description
 */
@Component
@Around("aroundInvocationHandler")
public class AnimalBean {

    @Value("${animal}")
    public String animalType;

    @NamedCat
    public String animalName() {
        return "This animal is " + animalType + ", no named";
    }

    public String animalInfo() {
        return "This animal is " + animalType;
    }
}
