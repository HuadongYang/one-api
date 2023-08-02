package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ParseException;

public abstract class AbstractColumnType {
    protected ColumnModel column;
    protected Class<?> type;

    public AbstractColumnType(ColumnModel column) {
        if (column == null) {
            throw new ParseException("column is null");
        }
        this.column = column;
    }

    public ColumnModel getColumn(){
        return column;
    }

    public Class<?> getType() {
        if (this.type == null) {
            try {
                type = Class.forName(column.getJavaType());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return this.type;
    }
}
