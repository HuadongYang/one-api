package com.yz.oneapi.utils.date;

import com.yz.oneapi.utils.StringPool;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/17 9:28
 * @describe
 */
public class TimeInterval extends GroupTimeInterval {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ID = StringPool.EMPTY;

    /**
     * 构造，默认使用毫秒计数
     */
    public TimeInterval() {
        this(false);
    }

    /**
     * 构造
     *
     * @param isNano 是否使用纳秒计数，false则使用毫秒
     */
    public TimeInterval(boolean isNano) {
        super(isNano);
        start();
    }

    /**
     * @return 开始计时并返回当前时间
     */
    public long start() {
        return start(DEFAULT_ID);
    }


    //----------------------------------------------------------- Interval

    /**
     * 从开始到当前的间隔时间（毫秒数）<br>
     * 如果使用纳秒计时，返回纳秒差，否则返回毫秒差
     *
     * @return 从开始到当前的间隔时间（毫秒数）
     */
    public long interval() {
        return interval(DEFAULT_ID);
    }


}
