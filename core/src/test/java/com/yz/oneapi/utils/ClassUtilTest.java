package com.yz.oneapi.utils;

import org.junit.jupiter.api.Test;

class ClassUtilTest {

    @Test
    public void loadByteArr(){
        Class<?> aClass = ClassUtil.loadClass("int", null, true);
        System.out.println(aClass);
    }
}