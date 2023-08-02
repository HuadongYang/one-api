package com.yz.oneapi.config;

import com.yz.oneapi.utils.StringUtil;

public class OneApiException extends RuntimeException {
    public OneApiException() {
    }

    public OneApiException(String message) {
        super(message);
    }

    public OneApiException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public OneApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneApiException(Throwable cause) {
        super(cause);
    }
}
