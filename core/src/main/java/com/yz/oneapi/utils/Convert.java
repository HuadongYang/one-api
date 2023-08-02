package com.yz.oneapi.utils;

import com.yz.oneapi.config.OneApiException;

import java.util.Calendar;
import java.util.Date;

public class Convert {
    public static Integer convertInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else if (value instanceof Date) {
            return (int) ((Date) value).getTime();
        } else if (value instanceof Calendar) {
            return (int) ((Calendar) value).getTimeInMillis();
        } else if (value instanceof String) {
            return Integer.valueOf((String) value);
        }
        throw new OneApiException("convertInt error: " + value.getClass());
    }
}
