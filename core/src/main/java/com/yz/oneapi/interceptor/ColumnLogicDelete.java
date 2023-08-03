package com.yz.oneapi.interceptor;

public class ColumnLogicDelete {
    //逻辑删除的字段
    private ColumnIdx source;
    //删除后字段的值
    private Object deleteValue;

    public ColumnLogicDelete() {
    }

    public ColumnLogicDelete(ColumnIdx source, Object deleteValue) {
        this.source = source;
        this.deleteValue = deleteValue;
    }

    public boolean isTable(String tableAlias) {
        return tableAlias.matches(source.getTableAliasRegex());
    }

    public ColumnIdx getSource() {
        return source;
    }

    public void setSource(ColumnIdx source) {
        this.source = source;
    }

    public Object getDeleteValue() {
        return deleteValue;
    }

    public void setDeleteValue(Object deleteValue) {
        this.deleteValue = deleteValue;
    }
}
