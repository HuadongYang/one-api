package com.yz.oneapi.repo;

import java.util.List;

public interface Persistence {

    <T> void write(String id, T t);

    default <T> byte[] serialize(T t){
        return null;
    };
    default <T> T deserialize(byte[] data, Class<T> clazz){
        return null;
    }
    <T> T read(String id, Class<T> clazz);
    <T> List<T> readList(String id, Class<T> clazz);
}
