package com.yz.oneapi.parser;

import com.yz.oneapi.utils.StringUtil;

public enum OrderEnum {
    ASC("asc"),
    DESC("desc");

    private String code;

    OrderEnum(String code) {
        this.code = code;
    }

    public static OrderEnum getByCode(String code){
        for (OrderEnum item : values()) {
            if (StringUtil.equals(item.code, code, true)){
                return item;
            }
        }
        return null;
    }
}
