package com.yz.oneapi.orm.executor;

import com.yz.oneapi.config.OneApiException;

public class ExecutorException extends OneApiException {

    public ExecutorException() {
        super();
    }

    public ExecutorException(String message) {
        super(message);
    }

    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutorException(Throwable cause) {
        super(cause);
    }
}
