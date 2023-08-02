package com.yz.oneapi.orm.reflection;

import com.yz.oneapi.orm.reflection.factory.DefaultObjectFactory;
import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import com.yz.oneapi.orm.reflection.wrapper.ObjectWrapperFactory;

public class SystemMetaObject {
    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(new NullObject(), DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    private static class NullObject {
    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }
}
