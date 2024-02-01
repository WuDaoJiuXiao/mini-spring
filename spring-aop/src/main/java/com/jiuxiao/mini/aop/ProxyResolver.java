package com.jiuxiao.mini.aop;

import net.bytebuddy.ByteBuddy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 14:02
 * @Description 代理对象解析器
 */
public class ProxyResolver {
    
    private final Logger logger = LoggerFactory.getLogger(ProxyResolver.class);
    
    /* 使用该第三方库动态生成字节码，代替 CGLIB */
    private final ByteBuddy byteBuddy = new ByteBuddy();
    
    
}
