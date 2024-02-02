package com.jiuxiao.mini.secret;

import com.jiuxiao.mini.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecretInvocationHandler implements InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(SecretInvocationHandler.class);

    public Map<String, String> secretMap = new HashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Secret secret = method.getAnnotation(Secret.class);
        if (secret == null) {
            return method.invoke(proxy, args);
        }
        String argsName = secret.value();
        try {
            return method.invoke(proxy, args);
        } finally {
            String secretType = null;
            if (argsName.equals("md5")) {
                secretType = "md5";
            }
            logger.info("Method {} secret by {}", method.getName(), secretType);
            secretMap.put(method.getName(), secretType);
        }
    }
}
