package com.yz.oneapi.utils.seq;

import com.yz.oneapi.config.OneApiException;

public class SeqException extends OneApiException {
    public SeqException() {
        super();
    }

    public SeqException(String message) {
        super(message);
    }

    public SeqException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeqException(Throwable cause) {
        super(cause);
    }
}

