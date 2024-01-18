package com.jiuxiao.ioc.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarFile;

/**
 * @Author 悟道九霄
 * @Date 2024/1/15 14:06
 * @Description 资源解析器，模拟 @ComponentScan 注解
 */
public class ResourceResolver {

    private String basePackage;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * @param mapper  映射函数
     * @param scanJar 是否扫描 jar 包
     * @return: java.util.List<R>
     * @description 从指定包下及其 jars 中扫描并返回所有的 class 文件的全限定名
     * @date 2024/1/17 17:00
     */
    public <R> List<R> findClass(Function<Resource, R> mapper, boolean scanJar) {
        basePackage = basePackage.replace(".", "/");
        ArrayList<R> clazzList = new ArrayList<>();
        try {
            Enumeration<URL> resources = getClassLoader().getResources(basePackage);
            if (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                URI uri = url.toURI();
                String uriStr = removeSuffixSlash(uri2String(uri));
                String parentUri = uriStr.substring(0, uriStr.length() - basePackage.length());
                if (parentUri.startsWith("file:")) {
                    parentUri = parentUri.substring(6);
                    scanClass(parentUri, mapper, clazzList, scanJar);
                }
            }
            return clazzList;
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        } catch (URISyntaxException ue) {
            throw new RuntimeException(ue);
        }
    }

    /**
     * @param parent    父路径
     * @param mapper    映射函数
     * @param clazzList 结果集合
     * @param scanJar   是否扫描 jar 包
     * @return: void
     * @description 扫描包路径下、jar包下的 class 文件并添加到结果集合
     * @date 2024/1/17 16:59
     */
    private <R> void scanClass(String parent, Function<Resource, R> mapper, ArrayList<R> clazzList, boolean scanJar) throws IOException {
        parent = removeSuffixSlash(parent);
        parent = removePrefixSlash(parent);
        Path rootDir = Paths.get(parent);
        String finalParent = parent;
        Files.walk(rootDir).filter(Files::isRegularFile).forEach(file -> {
            String absPath = file.toString();
            String[] split = absPath.split("\\.");
            String suffixName = split[split.length - 1];
            // 收集扫描结果，创建资源对象
            // Class.forName() 加载时，类的全限定名 filePath 不需要传最后的 .class 字段
            if (suffixName.equals("class")) {
                String pkgSlashName = absPath.substring(finalParent.length() + 1);
                String pkgDotName = slash2Dot(pkgSlashName);
                String filePath = pkgDotName.substring(0, pkgDotName.length() - 6);
                String fileName = filePath.substring(filePath.lastIndexOf(".") + 1);
                Resource resource = new Resource(fileName, filePath);
                R r = mapper.apply(resource);
                if (r != null) {
                    clazzList.add(r);
                }
            } else if (suffixName.equals("jar") && scanJar) {
                List<String> jarClass;
                try {
                    jarClass = readJarFileForClass(absPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                jarClass.forEach(clazzPath -> {
                    String clazzName = clazzPath.substring(clazzPath.lastIndexOf(".") + 1);
                    Resource resource = new Resource(clazzName, clazzPath);
                    R r = mapper.apply(resource);
                    if (r != null) {
                        clazzList.add(r);
                    }
                });
            }
        });
    }

    /**
     * @param absPath jar包路径
     * @return: java.util.List<java.lang.String>
     * @description 读取 jar 包中的 class 文件，收集全限定名
     * @date 2024/1/16 14:20
     */
    private List<String> readJarFileForClass(String absPath) throws IOException {
        JarFile jarFile;
        List<String> resClass = new ArrayList<>();
        jarFile = new JarFile(absPath);
        jarFile.stream().forEach(res -> {
            String resAbsPath = res.toString();
            if (resAbsPath.endsWith(".class")) {
                String doted = slash2Dot(resAbsPath);
                String filePath = doted.substring(0, doted.length() - 6);
                resClass.add(filePath);
            }
        });
        jarFile.close();
        return resClass;
    }

    /**
     * @return: java.lang.ClassLoader
     * @description 获取资源加载器实例
     * @date 2024/1/15 14:52
     */
    private ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        return loader;
    }

    /**
     * @param s 源文件路径
     * @return: java.lang.String
     * @description 将文件路径中的斜杠替换为点
     * @date 2024/1/15 17:03
     */
    private String slash2Dot(String s) {
        s = s.replace("\\", ".");
        s = s.replace("/", ".");
        return s;
    }

    /**
     * @param uri uri变量
     * @return: java.lang.String
     * @description 将 uri 转为 String 类型
     * @date 2024/1/15 15:25
     */
    private String uri2String(URI uri) throws UnsupportedEncodingException {
        return URLDecoder.decode(uri.toString(), String.valueOf(StandardCharsets.UTF_8));
    }

    /**
     * @param path 待处理路径
     * @return: java.lang.String
     * @description 移除路径开头的斜杠、反斜杠
     * @date 2024/1/15 14:59
     */
    private String removePrefixSlash(String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * @param path 待处理路径
     * @return: java.lang.String
     * @description 移除路径末尾的斜杠、反斜杠
     * @date 2024/1/15 14:57
     */
    private String removeSuffixSlash(String path) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
