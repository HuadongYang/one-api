package com.yz.oneapi.parser.expr.function;

import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.expr.select.SelectExpr;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.StringUtil;

public class CountFunction implements Function, SelectExpr {
    private final Expression expr;

    public static final String SQL_KEY = "COUNT(?)";

    public CountFunction(Expression columnExpr) {
        this.expr = columnExpr;
    }


    @Override
    public String sqlSegment() {
        return StringUtil.formatWith(SQL_KEY, PLACEHOLDER, expr.sqlSegment());
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return sqlSegment();
    }


}
