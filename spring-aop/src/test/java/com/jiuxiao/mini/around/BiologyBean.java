package com.jiuxiao.mini.around;

import com.jiuxiao.mini.annotation.Autowired;
import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Order;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 15:34
 * @Description
 */
@Order(0)
@Component
public class BiologyBean {

    public AnimalBean animalBean;

    public BiologyBean(@Autowired AnimalBean animalBean) {
        this.animalBean = animalBean;
    }
}
