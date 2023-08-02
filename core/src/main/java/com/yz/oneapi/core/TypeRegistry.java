package com.yz.oneapi.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeRegistry {
    public final Map<String, Class<?>> map = new HashMap<>();

    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final TypeRegistry INSTANCE = new TypeRegistry();
    }

    private TypeRegistry() {
        register();
    }

    public static TypeRegistry getInstance() {
        return TypeRegistry.SingletonHolder.INSTANCE;
    }

    public Class<?> getType(String javaType){
        if (map.containsKey(javaType)) {
            return map.get(javaType);
        }
        try {
            return Class.forName(javaType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void register() {
        map.put("java.lang.String", String.class);
        map.put("java.math.BigDecimal", BigDecimal.class);
        map.put("java.math.BigInteger", BigInteger.class);
        map.put("java.lang.Boolean", Boolean.class);
        map.put("java.lang.Character", Character.class);
        map.put("java.util.Date", Date.class);
        map.put("java.lang.Double", Double.class);
        map.put("java.lang.Float", Float.class);
        map.put("java.lang.Integer", Integer.class);
        map.put("java.time.LocalDateTime", LocalDateTime.class);
        map.put("java.time.LocalDate", LocalDate.class);
        map.put("java.time.LocalTime", LocalTime.class);
        map.put("java.lang.Long", Long.class);
        map.put("java.time.Month", Month.class);
        map.put("java.time.OffsetTime", OffsetTime.class);
        map.put("java.time.OffsetDateTime", OffsetDateTime.class);
        map.put("java.lang.Short", Short.class);
        map.put("java.time.String", Year.class);
    }
}
