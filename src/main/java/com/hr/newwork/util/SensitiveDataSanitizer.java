package com.hr.newwork.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class SensitiveDataSanitizer {
    private static final String MASK = "*****";
    private static final String[] SENSITIVE_KEYS = {"password", "pass", "pwd", "secret", "token", "key", "credentials", "auth", "apiKey", "accessToken", "refreshToken", "passwordHash"};

    public static Object sanitizeObject(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Map<?, ?> map) {
            Map<Object, Object> sanitized = new java.util.HashMap<>();
            for (var entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey()).toLowerCase();
                if (isSensitiveKey(key)) {
                    sanitized.put(entry.getKey(), MASK);
                } else {
                    sanitized.put(entry.getKey(), sanitizeObject(entry.getValue()));
                }
            }
            return sanitized;
        } else if (obj instanceof Collection<?> col) {
            Collection<Object> sanitized = obj instanceof java.util.List ? new java.util.ArrayList<>() : new java.util.HashSet<>();
            for (Object item : col) {
                sanitized.add(sanitizeObject(item));
            }
            return sanitized;
        } else if (obj.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(obj);
            Object[] sanitized = new Object[len];
            for (int i = 0; i < len; i++) {
                sanitized[i] = sanitizeObject(java.lang.reflect.Array.get(obj, i));
            }
            return sanitized;
        } else if (obj instanceof String str) {
            if (isSensitiveString(str)) return MASK;
            return str;
        } else if (isJavaBean(obj)) {
            try {
                Class<?> clazz = obj.getClass();
                Object clone = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (isSensitiveKey(field.getName())) {
                        field.set(clone, MASK);
                    } else {
                        field.set(clone, sanitizeObject(value));
                    }
                }
                return clone;
            } catch (Exception e) {
                return obj.toString();
            }
        }
        return obj;
    }

    public static String sanitizeArgs(Object[] args) {
        Object[] sanitized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            sanitized[i] = sanitizeObject(args[i]);
        }
        return java.util.Arrays.toString(sanitized);
    }

    private static boolean isSensitiveKey(String key) {
        for (String sensitive : SENSITIVE_KEYS) {
            if (key.toLowerCase().contains(sensitive)) return true;
        }
        return false;
    }

    private static boolean isSensitiveString(String str) {
        // Mask if string looks like a hash/token/key (long, random, or base64-like)
        return str.length() > 20 && str.matches("[A-Za-z0-9+/=]+") && !str.matches("[a-zA-Z ]+");
    }

    private static boolean isJavaBean(Object obj) {
        String pkg = obj.getClass().getPackageName();
        return pkg.startsWith("com.hr.newwork") && !(obj instanceof Enum);
    }
}

