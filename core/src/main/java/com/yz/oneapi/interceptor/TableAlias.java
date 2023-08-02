package com.yz.oneapi.interceptor;

import java.util.List;

public class TableAlias {

    private String tableName;
    private String tableAlias;
    private List<ColumnAlias> columns;

    public TableAlias() {
    }

    public TableAlias(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public static class ColumnAlias {
        private String columnName;
        private String columnAlias;

        private String javaType;

        public ColumnAlias() {
        }

        public ColumnAlias(String columnName, String columnAlias) {
            this.columnName = columnName;
            this.columnAlias = columnAlias;
        }

        public ColumnAlias(String columnName, String columnAlias, String javaType) {
            this.columnName = columnName;
            this.columnAlias = columnAlias;
            this.javaType = javaType;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnAlias() {
            return columnAlias;
        }

        public void setColumnAlias(String columnAlias) {
            this.columnAlias = columnAlias;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public List<ColumnAlias> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnAlias> columns) {
        this.columns = columns;
    }
}
