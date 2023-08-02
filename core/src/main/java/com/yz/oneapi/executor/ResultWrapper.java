package com.yz.oneapi.executor;

import java.util.LinkedHashMap;

public class ResultWrapper extends LinkedHashMap<String, Object> {

    public void add(String table, Page page){
        if (page.isPage()) {
            put(table, page);
        }else {
            put(table, page.getData());
        }
    }

    public void add(String table, Integer change) {
        put(table, change);
    }

}
