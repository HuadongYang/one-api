package com.yz.oneapi.parser.expr;

import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

public class ParenthesisExpr implements Expression, Builder {

    private ConditionChain chain;
    private boolean close;

    public ParenthesisExpr() {
        chain = new ConditionChain();
        chain.setParent(this);
        close = false;
    }

    public ConditionChain getChain() {
        return chain;
    }

    public boolean isClose() {
        return close;
    }

    public void close() {
        close = true;
    }


    @Override
    public String sqlSegment() {
        return "(" + chain + ")";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }


    public ConditionChain getLinkableNode() {
        if (close) {
            return chain;
        }
        if (chain.size() == 0) {
            return chain;
        }
        return chain.getLinkableChain();
    }

    @Override
    public String toString() {
        return "(" + chain.toString() + ")";
    }
}
