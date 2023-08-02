package com.yz.oneapi.parser;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.utils.StringUtil;

public class ParseException extends OneApiException {
    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
