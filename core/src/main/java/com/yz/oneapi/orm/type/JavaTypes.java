package com.yz.oneapi.orm.type;

import com.yz.oneapi.utils.Lists;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum JavaTypes {

    STRING(String.class, Lists.newArrayList(JdbcType.VARCHAR)),
    BIGINTEGER(BigInteger.class, Lists.newArrayList(JdbcType.BIGINT));

    private final Class<?> valueClass;
    private final List<JdbcType> jdbcTypes;

    private static Map<JdbcType,Class<?>> codeLookup = new HashMap<>();

    static {
        for (JavaTypes type : JavaTypes.values()) {
            for ( JdbcType jdbcType : type.jdbcTypes){
                codeLookup.put(jdbcType, type.valueClass);
            }
        }
    }

    JavaTypes(Class<?> valueClass, List<JdbcType> jdbcTypes) {
        this.valueClass = valueClass;
        this.jdbcTypes = jdbcTypes;
    }



    public static Class<?> getClass(JdbcType jdbcType) {
        return codeLookup.get(jdbcType);
    }
}
