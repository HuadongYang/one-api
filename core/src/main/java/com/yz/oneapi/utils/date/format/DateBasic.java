package com.yz.oneapi.utils.date.format;

import java.util.Locale;
import java.util.TimeZone;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 18:20
 * @describe
 */
public interface DateBasic {
    String getPattern();

    /**
     * 获得时区
     *
     * @return {@link TimeZone}
     */
    TimeZone getTimeZone();

    /**
     * 获得 日期地理位置
     *
     * @return {@link Locale}
     */
    Locale getLocale();
}
