package com.yz.oneapi.orm.session;

public interface ResultHandler<T> {

    void handleResult(ResultContext<? extends T> resultContext);

}
