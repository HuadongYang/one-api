package com.yz.oneapi.orm.reflection.wrapper;

import com.yz.oneapi.orm.reflection.MetaClass;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.reflection.ReflectionException;
import com.yz.oneapi.orm.reflection.SystemMetaObject;
import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.reflection.invoker.Invoker;
import com.yz.oneapi.orm.reflection.property.PropertyTokenizer;
import com.yz.oneapi.utils.ExceptionUtil;

import java.util.List;

public class BeanWrapper extends BaseWrapper {

    private final Object object;
    private final MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject);
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass(), metaObject.getReflectorFactory());
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, object);
            return getCollectionValue(prop, collection);
        }
        return getBeanProperty(prop, object);
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, object);
            setCollectionValue(prop, collection, value);
        } else {
            setBeanProperty(prop, object, value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping, Character namingStyle) {
        return metaClass.findProperty(name, useCamelCaseMapping, namingStyle);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.getSetterType(name);
        }
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return metaClass.getSetterType(name);
        } else {
            return metaValue.getSetterType(prop.getChildren());
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.getGetterType(name);
        }
        MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
        if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
            return metaClass.getGetterType(name);
        } else {
            return metaValue.getGetterType(prop.getChildren());
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.hasSetter(name);
        }
        if (metaClass.hasSetter(prop.getIndexedName())) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.hasSetter(name);
            } else {
                return metaValue.hasSetter(prop.getChildren());
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (!prop.hasNext()) {
            return metaClass.hasGetter(name);
        }
        if (metaClass.hasGetter(prop.getIndexedName())) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.hasGetter(name);
            } else {
                return metaValue.hasGetter(prop.getChildren());
            }
        } else {
            return false;
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        MetaObject metaValue;
        Class<?> type = getSetterType(prop.getName());
        try {
            Object newObject = objectFactory.create(type);
            metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(),
                    metaObject.getReflectorFactory());
            set(prop, newObject);
        } catch (Exception e) {
            throw new ReflectionException("Cannot set value of property '" + name + "' because '" + name
                    + "' is null and cannot be instantiated on instance of " + type.getName() + ". Cause:" + e.toString(), e);
        }
        return metaValue;
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        try {
            Invoker method = metaClass.getGetInvoker(prop.getName());
            try {
                return method.invoke(object, NO_ARGUMENTS);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectionException(
                    "Could not get property '" + prop.getName() + "' from " + object.getClass() + ".  Cause: " + t.toString(), t);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        try {
            Invoker method = metaClass.getSetInvoker(prop.getName());
            Object[] params = { value };
            try {
                method.invoke(object, params);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (Throwable t) {
            throw new ReflectionException("Could not set property '" + prop.getName() + "' of '" + object.getClass()
                    + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> list) {
        throw new UnsupportedOperationException();
    }

}

