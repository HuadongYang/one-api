package com.yz.oneapi.utils;

import com.yz.oneapi.orm.type.BaseTypeHandler;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JavaTypeUtils {

    public static String getJavaType(String type, BigInteger length, Integer dataDot, TypeHandlerRegistry typeHandlerRegistry) {
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
            int intVal = length.intValue();
            if (intVal == 1) {
                return Boolean.class.getName();
            } else if (intVal == 2) {
                return Byte.class.getName();
            } else if (intVal == 3 || intVal == 4) {
                return Short.class.getName();
            } else if (intVal >= 5 && intVal <= 9) {
                return Integer.class.getName();
            } else if (intVal >= 10 && intVal <= 18) {
                return Long.class.getName();
            } else if (intVal >= 19 && intVal <= 38) {
                return BigDecimal.class.getName();
            }
            return BigDecimal.class.getName();
        }
        return null;
    }
}
