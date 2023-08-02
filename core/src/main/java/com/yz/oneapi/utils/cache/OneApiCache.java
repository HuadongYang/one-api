package com.yz.oneapi.utils.cache;

import java.time.Duration;

public interface OneApiCache {
    /**
     * 过期存储
     *
     * @param key     索引
     * @param value   值
     * @param timeout 过期时间
     */
    void put(Object key, Object value, Duration timeout);

    /**
     * 过期存储
     *
     * @param key     索引
     * @param value   值
     * @param seconds 过期时间 秒
     */
    void put(Object key, Object value, long seconds);


}
