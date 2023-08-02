package com.yz.oneapi.utils.convert;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.utils.StringUtil;

public class ConvertException extends OneApiException {
    public ConvertException() {
        super();
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }
}