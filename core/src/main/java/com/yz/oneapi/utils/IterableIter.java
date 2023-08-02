package com.yz.oneapi.utils;

import java.util.Iterator;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 9:18
 * @describe
 */
public interface IterableIter<T> extends Iterable<T>, Iterator<T> {
    @Override
    default Iterator<T> iterator() {
        return this;
    }
}