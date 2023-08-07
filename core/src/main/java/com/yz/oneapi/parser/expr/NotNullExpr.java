package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Ordered;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;

public class NotNullExpr extends AbstractExpr implements Ordered {

    public static final String SQL_KEY = "IS NOT NULL";

    public NotNullExpr(ColumnModel column, Object value) {
        super(column, value);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
    @Override
    void parse() {

    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String keyword() {
        return SQL_KEY;
    }

    @Override
    public String sqlSegment() {
        return column.getColumn() + CharPool.SPACE + SQL_KEY;
    }



    @Override
    public String toString() {
        return sqlSegment();
    }
}
