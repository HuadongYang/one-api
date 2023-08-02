package com.yz.oneapi.orm.executor.result;

import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.session.ResultContext;
import com.yz.oneapi.orm.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultResultHandler implements ResultHandler<Object> {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext<?> context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }

}
