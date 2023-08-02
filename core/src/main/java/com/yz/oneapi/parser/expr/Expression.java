package com.yz.oneapi.parser.expr;

import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public interface Expression {
    String SPLIT = ",";
    String PLACEHOLDER = "?";



    String sqlSegment();

    void accept(SqlAstVisitor v);



}
