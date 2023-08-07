package com.yz.oneapi.parser.ast;

import com.yz.oneapi.parser.expr.Builder;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public class Table implements Builder, Expression {
    private String name;
    private String alias;

    public Table(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String sqlSegment() {
        return null;
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
}
