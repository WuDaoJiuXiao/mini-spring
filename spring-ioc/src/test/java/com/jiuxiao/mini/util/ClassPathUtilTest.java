package com.jiuxiao.mini.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ClassPathUtilTest {

    @Test
    public void testReadInputStream() throws Exception {
        String path = "./testBanner.txt";
        InputStreamCallback<String> streamCallback = inputStream -> {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        };
        String read = ClassPathUtil.readInputStream(path, streamCallback);
        Assert.assertNotNull(read);
        Assert.assertEquals(read, "test banner content");
    }

    @Test
    public void testReadString() throws Exception {
        String path = "./testBanner.txt";
        String result = ClassPathUtil.readString(path);
        Assert.assertNotNull(result);
        Assert.assertEquals("test banner content", result);
    }
}