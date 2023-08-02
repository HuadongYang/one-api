package com.yz.oneapi.interceptor;

public class ColumnIdx {
    private String tableAliasRegex;
    private String columnAlias;

    public ColumnIdx() {
    }

    public ColumnIdx(String tableAliasRegex, String columnAlias) {
        this.tableAliasRegex = tableAliasRegex;
        this.columnAlias = columnAlias;
    }

    public String getTableAliasRegex() {
        return tableAliasRegex;
    }

    public void setTableAliasRegex(String tableAliasRegex) {
        this.tableAliasRegex = tableAliasRegex;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }
}
