package com.jiuxiao.mini.secret;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Secret("secretInvocationHandler")
public abstract class BaseWorker {

    @Secret("md5")
    public String secretStr(String message) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder("0x");
        for (byte b : digest) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
