package com.yz.oneapi.utils.date;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 18:23
 * @describe
 */
public class TemporalUtil {
    public static Duration between(Temporal startTimeInclude, Temporal endTimeExclude) {
        return Duration.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为时间差的long值
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @param unit             时间差单位
     * @return 时间差
     */
    public static long between(Temporal startTimeInclude, Temporal endTimeExclude, ChronoUnit unit) {
        return unit.between(startTimeInclude, endTimeExclude);
    }

}
