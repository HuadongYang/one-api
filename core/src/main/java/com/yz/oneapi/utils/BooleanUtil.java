package com.yz.oneapi.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BooleanUtil {
    /**
     * 表示为真的字符串
     */
    private static final Set<String> TRUE_SET = new HashSet<>();
    /**
     * 表示为假的字符串
     */
    private static final Set<String> FALSE_SET = new HashSet<>();

    static {
        Collections.addAll(TRUE_SET, "true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√");
        Collections.addAll(FALSE_SET, "false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×");
    }
    /**
     * boolean值转为double
     *
     * @param value Boolean值
     * @return double值
     */
    public static double toDouble(boolean value) {
        return toInt(value);
    }

    /**
     * boolean值转为double
     *
     * @param value Boolean值
     * @return double值
     */
    public static Double toDoubleObj(boolean value) {
        return toDouble(value);
    }

    /**
     * boolean值转为long
     *
     * @param value Boolean值
     * @return long值
     */
    public static long toLong(boolean value) {
        return toInt(value);
    }

    /**
     * boolean值转为Long
     *
     * @param value Boolean值
     * @return Long值
     */
    public static Long toLongObj(boolean value) {
        return toLong(value);
    }
    /**
     * boolean值转为float
     *
     * @param value Boolean值
     * @return float值
     */
    public static float toFloat(boolean value) {
        return (float) toInt(value);
    }

    /**
     * boolean值转为Float
     *
     * @param value Boolean值
     * @return float值
     */
    public static Float toFloatObj(boolean value) {
        return toFloat(value);
    }

    /**
     * boolean值转为Integer
     *
     * @param value Boolean值
     * @return Integer值
     */
    public static Integer toInteger(boolean value) {
        return toInt(value);
    }

    /**
     * boolean值转为short
     *
     * @param value Boolean值
     * @return short值
     */
    public static short toShort(boolean value) {
        return (short) toInt(value);
    }

    /**
     * boolean值转为Short
     *
     * @param value Boolean值
     * @return Short值
     */
    public static Short toShortObj(boolean value) {
        return toShort(value);
    }

    /**
     * boolean值转为Byte
     *
     * @param value Boolean值
     * @return Byte值
     */
    public static Byte toByteObj(boolean value) {
        return toByte(value);
    }

    /**
     * boolean值转为byte
     *
     * @param value Boolean值
     * @return byte值
     */
    public static byte toByte(boolean value) {
        return (byte) toInt(value);
    }


    /**
     * boolean值转为Character
     *
     * @param value Boolean值
     * @return Character值
     */
    public static Character toCharacter(boolean value) {
        return toChar(value);
    }

    /**
     * boolean值转为char
     *
     * @param value Boolean值
     * @return char值
     */
    public static char toChar(boolean value) {
        return (char) toInt(value);
    }

    /**
     * boolean值转为int
     *
     * @param value Boolean值
     * @return int值
     */
    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * 转换字符串为boolean值
     *
     * @param valueStr 字符串
     * @return boolean值
     */
    public static boolean toBoolean(String valueStr) {
        if (OneApiUtil.isNotBlank(valueStr)) {
            valueStr = valueStr.trim().toLowerCase();
            return TRUE_SET.contains(valueStr);
        }
        return false;
    }


}
