package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiException;

public class MappingException extends OneApiException {

    private static final long serialVersionUID = 7642570221267566591L;

    public MappingException() {
        super();
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }
}