package com.yz.oneapi.parser.visitor;

import com.yz.oneapi.executor.Page;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.ast.InsertAst;
import com.yz.oneapi.parser.ast.SelectItems;
import com.yz.oneapi.parser.ast.Table;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.*;
import com.yz.oneapi.parser.expr.function.CountFunction;
import com.yz.oneapi.parser.expr.select.AsteriskExpr;
import com.yz.oneapi.parser.expr.select.ColumnExpr;

public interface SqlAstVisitor {
    void visit(ColumnExpr columnExpr);
    void visit(CountFunction countFunction);
    void visit(AsteriskExpr asteriskExpr);
    void visit(Page page);
    void visit(Order order);
    void visit(ConditionChain chain);
    void visit(BetweenExpr expr);
    void visit(ConstantExpr expr);
    void visit(EqualExpr expr);
    void visit(GreaterEqualExpr expr);
    void visit(GreaterThanExpr expr);
    void visit(InExpr expr);
    void visit(LessThanExpr expr);
    void visit(LessEqualExpr expr);
    void visit(LikeExpr expr);
    void visit(NotEqualExpr expr);
    void visit(NotNullExpr expr);
    void visit(NullExpr expr);
    void visit(OrExpr expr);
    void visit(AndExpr expr);
    void visit(ParenthesisExpr expr);
    void visit(ReferExpr expr);
    void visit(Table expr);
    void visit(SelectItems selectItems);
    void visit(InsertAst insertAst);
}
