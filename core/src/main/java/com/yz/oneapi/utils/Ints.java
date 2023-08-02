package com.yz.oneapi.utils;

public class Ints {
    public static int saturatedCast(long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        } else {
            return value < -2147483648L ? Integer.MIN_VALUE : (int)value;
        }
    }
}
