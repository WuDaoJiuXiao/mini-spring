package com.jiuxiao.mini.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description 输入流的回调函数
 * @Author 悟道九霄
 * @Date 2024/1/18 14:45
 */
@FunctionalInterface
public interface InputStreamCallback<T> {

    T doWithInputStream(InputStream inputStream) throws IOException;
}
