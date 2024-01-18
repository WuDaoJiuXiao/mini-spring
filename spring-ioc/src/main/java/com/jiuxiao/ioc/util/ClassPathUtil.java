package com.jiuxiao.ioc.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Author 悟道九霄
 * @Date 2024/1/18 14:46
 * @Description classpath 变量资源加载工具类
 */
public class ClassPathUtil {

    /**
     * @param path     要读取的文件在 classpath 的路径
     * @param callback 回调函数
     * @return: T
     * @description 读取 classpath 下的配置文件
     * @date 2024/1/18 14:51
     */
    public static <T> T readInputStream(String path, InputStreamCallback<T> callback) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        InputStream inputStream;
        try {
            inputStream = getContextClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }
            return callback.doWithInputStream(inputStream);
        } catch (IOException ie) {
            throw new UncheckedIOException(ie);
        }
    }

    /**
     * @param path 读取的文件在 classpath 的路径
     * @return: java.lang.String
     * @description 读取纯字符串
     * @date 2024/1/18 14:59
     */
    public static String readString(String path) {
        return readInputStream(path, (inputStream) -> {
            byte[] bytes = readBytes(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        });
    }

    /**
     * @param inputStream 输入流
     * @return: byte[]
     * @description 读取输入流为字节数组
     * @date 2024/1/18 14:58
     */
    private static byte[] readBytes(InputStream inputStream) {
        int read;
        byte[] buffer;
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            byteArrayOutputStream.close();
        }catch (IOException ie){
            throw new UncheckedIOException(ie);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * @return: java.lang.ClassLoader
     * @description 获取上下文加载器
     * @date 2024/1/18 14:53
     */
    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassPathUtil.class.getClassLoader();
        }
        return classLoader;
    }
}
