package com.yz.oneapi.utils.date;

import java.time.ZoneId;
import java.util.TimeZone;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 18:23
 * @describe
 */
public class ZoneUtil {
    public static TimeZone toTimeZone(ZoneId zoneId) {
        if (null == zoneId) {
            return TimeZone.getDefault();
        }

        return TimeZone.getTimeZone(zoneId);
    }

}
