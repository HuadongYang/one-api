package com.yz.oneapi.parser.expr;

import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public class AndExpr implements Expression, Builder{

    @Override
    public String sqlSegment() {
        return "AND";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

}
