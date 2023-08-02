package com.yz.oneapi.orm.reflection;

public interface ReflectorFactory {
    boolean isClassCacheEnabled();

    void setClassCacheEnabled(boolean classCacheEnabled);

    Reflector findForClass(Class<?> type);
}
