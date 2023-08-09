package com.yz.oneapi.parser.chain;

import com.yz.oneapi.parser.expr.*;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

/**
 * 1、既是表达式，也是节点
 * 2、节点引用表达式
 */
public class ConditionChain extends AutoLinkedList<Expression> implements Expression, Builder {

    private Expression parent;

    //可链接的node
    public ConditionChain getLinkableChain() {
        if (size() == 0) {
            return this;
        }
        Expression last = getLast();
        if (last instanceof ParenthesisExpr && !((ParenthesisExpr) last).isClose()) {
            return ((ParenthesisExpr) last).getLinkableNode();
        } else {
            return this;
        }
    }

    public Expression getParent() {
        return parent;
    }

    public void setParent(Expression parent) {
        this.parent = parent;
    }

    @Override
    public String sqlSegment() {
        return null;
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }



    public boolean isAndOr(Expression expr) {
        return expr instanceof AndExpr || expr instanceof OrExpr;
    }



}
