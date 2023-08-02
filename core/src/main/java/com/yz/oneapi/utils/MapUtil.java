package com.yz.oneapi.utils;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class MapUtil {
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Map.entry(key, value) alternative for Java 8.
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    private MapUtil() {
        super();
    }
}
