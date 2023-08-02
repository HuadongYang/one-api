package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Ordered;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;

public class NullExpr extends AbstractExpr implements Ordered {
    public static final String SQL_KEY = "IS NULL";
    public NullExpr(ColumnModel column, Object value) {
        super(column, value);
    }

    public NullExpr(ColumnModel column) {
        super(column);
    }

    @Override
    void parse() {

    }


    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
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
    public int getOrder() {
        return 0;
    }

    @Override
    public String toString() {
        return sqlSegment();
    }
}
