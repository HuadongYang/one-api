package com.yz.oneapi.utils.date;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.utils.StringUtil;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/17 8:36
 * @describe
 */
public class DateException extends OneApiException {
    private static final long serialVersionUID = 8247610319171014183L;

    public DateException(Throwable e) {
        super(e);
    }

    public DateException(String message) {
        super(message);
    }

    public DateException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public DateException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DateException(Throwable throwable, String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params), throwable);
    }
}

