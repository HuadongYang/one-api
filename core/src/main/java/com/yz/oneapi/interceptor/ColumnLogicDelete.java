package com.yz.oneapi.interceptor;

public class ColumnLogicDelete {
    //逻辑删除的字段
    private ColumnIdx column;
    //删除后字段的值
    private Object deleteValue;

    public ColumnLogicDelete() {
    }

    public ColumnLogicDelete(ColumnIdx column, Object deleteValue) {
        this.column = column;
        this.deleteValue = deleteValue;
    }

    public boolean isTable(String tableAlias) {
        return tableAlias.matches(column.getTableAliasRegex());
    }

    public ColumnIdx getColumn() {
        return column;
    }

    public void setColumn(ColumnIdx column) {
        this.column = column;
    }

    public Object getDeleteValue() {
        return deleteValue;
    }

    public void setDeleteValue(Object deleteValue) {
        this.deleteValue = deleteValue;
    }
}
