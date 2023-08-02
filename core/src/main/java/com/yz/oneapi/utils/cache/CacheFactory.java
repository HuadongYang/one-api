package com.yz.oneapi.utils.cache;

public class CacheFactory {

    public static <K, V> LRUCache<K, V> newLRUCache(int capacity, long timeout) {
        return new LRUCache(capacity, timeout);
    }
    public static <K, V> LRUCache<K, V> newLRUCache(int capacity) {
        return new LRUCache(capacity);
    }
}
