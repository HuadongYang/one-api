package com.yz.oneapi.utils;

import com.yz.oneapi.orm.type.BaseTypeHandler;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;

import java.math.BigDecimal;

public class JavaTypeUtils {

    public static String getJavaType(String type, Integer length, Integer dataDot, TypeHandlerRegistry typeHandlerRegistry) {
        JdbcType jdbcType = JdbcType.forCode(type.toUpperCase());
        if (jdbcType != null) {
            BaseTypeHandler<?> typeHandler = (BaseTypeHandler) typeHandlerRegistry.getTypeHandler(jdbcType);
            if (typeHandler != null) {
                return typeHandler.getRawType().getTypeName();
            }
        }

        if ("NUMBER".equalsIgnoreCase(type)) {
            if (dataDot > 0) {
                return Double.class.getName();
            }
            if (length == 1) {
                return Boolean.class.getName();
            } else if (length == 2) {
                return Byte.class.getName();
            } else if (length == 3 || length == 4) {
                return Short.class.getName();
            } else if (length >= 5 && length <= 9) {
                return Integer.class.getName();
            } else if (length >= 10 && length <= 18) {
                return Long.class.getName();
            } else if (length >= 19 && length <= 38) {
                return BigDecimal.class.getName();
            }
            return BigDecimal.class.getName();
        }
        return null;
    }
}
