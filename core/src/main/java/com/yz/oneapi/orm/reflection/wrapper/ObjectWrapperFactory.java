package com.yz.oneapi.orm.reflection.wrapper;

import com.yz.oneapi.orm.reflection.MetaObject;

public interface ObjectWrapperFactory {

    boolean hasWrapperFor(Object object);

    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}

