package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.utils.ClassUtil;

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
            type = ClassUtil.loadClass(column.getJavaType());
        }
        return this.type;
    }
}
