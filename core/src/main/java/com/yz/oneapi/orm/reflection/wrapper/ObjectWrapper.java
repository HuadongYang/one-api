package com.yz.oneapi.orm.reflection.wrapper;

import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.reflection.property.PropertyTokenizer;

import java.util.List;

public interface ObjectWrapper {
    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);

    String findProperty(String name, boolean useCamelCaseMapping, Character namingStyle);

    String[] getGetterNames();

    String[] getSetterNames();

    Class<?> getSetterType(String name);

    Class<?> getGetterType(String name);

    boolean hasSetter(String name);

    boolean hasGetter(String name);

    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    boolean isCollection();

    void add(Object element);

    <E> void addAll(List<E> element);
}
