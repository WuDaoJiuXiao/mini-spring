package com.jiuxiao.mini;

import com.jiuxiao.mini.annotation.*;
import com.jiuxiao.mini.ioc.imported.DateConfiguration;
import com.jiuxiao.mini.ioc.imported.TimeConfiguration;

/**
 * @Author 悟道九霄
 * @Date 2024/1/28 14:40
 * @Description
 */
@ComponentScan
@Import({DateConfiguration.class, TimeConfiguration.class})
public class BeanScanApplication {
}
