package com.yz.oneapi.parser.expr;

import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public class ConstantExpr implements Expression {
    private Object val;

    public ConstantExpr(Object val) {
        this.val = val;
    }

    @Override
    public String sqlSegment() {
        return val.toString();
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
}
