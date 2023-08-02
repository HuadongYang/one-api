package com.yz.oneapi.repo;

import com.yz.oneapi.config.OneApiException;

public class PersistenceException extends OneApiException {
    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
