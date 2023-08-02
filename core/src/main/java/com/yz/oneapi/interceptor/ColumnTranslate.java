package com.yz.oneapi.interceptor;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 目前仅支持source为1个字段
 */
public class ColumnTranslate {
    //要翻译的字段
    private ColumnIdx source;
    //返回的key
    private String target;
    /**
     * 输入参数为source的值，输出参数为target的值
     * 适用于把1翻译为成功，0翻译为失败这种简单翻译
     */
    private Function<Object, Object> function;

    /**
     * 批量翻译，Set<Object>是source的不重复值，输出List<Object>为target的值，set和list要一一对应
     * 适用于需要去数据库里查询的耗时翻译
     * List请使用ArrayList
     */
    private Function<Set<Object>, List<Object>> batchFunction;

    public ColumnTranslate() {
    }

    public ColumnTranslate(ColumnIdx source, String target) {
        this.source = source;
        this.target = target;
    }
    public ColumnIdx getSource() {
        return source;
    }

    public void setSource(ColumnIdx source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Function<Object, Object> getFunction() {
        return function;
    }

    public void setFunction(Function<Object, Object> function) {
        this.function = function;
    }

    public Function<Set<Object>, List<Object>> getBatchFunction() {
        return batchFunction;
    }

    public void setBatchFunction(Function<Set<Object>, List<Object>> batchFunction) {
        this.batchFunction = batchFunction;
    }
}
