package com.yz.oneapi.parser.expr.select;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.expr.AbstractColumnType;
import com.yz.oneapi.parser.expr.Builder;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

import java.util.Objects;

public class ColumnExpr extends AbstractColumnType implements SelectExpr, Builder {


    public ColumnExpr(ColumnModel column) {
        super(column);
    }

    @Override
    public String sqlSegment() {
        return column.getColumn();
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    public String getColumnName(){
        return this.column.getColumn();
    }
    public String getColumnAlias(){
        return this.column.getAlias();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnExpr that = (ColumnExpr) o;
        return Objects.equals(column.getColumn(), that.column.getColumn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(column.getColumn());
    }
}
