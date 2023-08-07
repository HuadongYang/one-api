package com.yz.oneapi.parser.expr.select;

import com.yz.oneapi.parser.visitor.SqlAstVisitor;

/**
 * *号
 */
public class AsteriskExpr implements SelectExpr {

    @Override
    public String sqlSegment() {
        return "*";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
}
