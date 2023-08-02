package com.yz.oneapi.parser.expr;

import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public class OrExpr implements Expression, Builder{

    @Override
    public String sqlSegment() {
        return "OR";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

}
