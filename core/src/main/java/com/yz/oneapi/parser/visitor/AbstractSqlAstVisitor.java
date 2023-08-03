package com.yz.oneapi.parser.visitor;

import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.Parser;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.ast.SelectItems;
import com.yz.oneapi.parser.ast.Table;
import com.yz.oneapi.parser.chain.AutoLinkedList;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.*;
import com.yz.oneapi.parser.expr.function.CountFunction;
import com.yz.oneapi.parser.expr.select.AsteriskExpr;
import com.yz.oneapi.parser.expr.select.ColumnExpr;
import com.yz.oneapi.parser.expr.select.SelectExpr;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.StringPool;

import java.util.List;
import java.util.UUID;

public abstract class AbstractSqlAstVisitor implements SqlAstVisitor {

    protected PreparedSql preparedSql;

    public AbstractSqlAstVisitor(PreparedSql preparedSql) {
        this.preparedSql = preparedSql;
    }

    @Override
    public void visit(ColumnExpr columnExpr) {
        preparedSql.append(columnExpr.sqlSegment());
    }

    @Override
    public void visit(CountFunction countFunction) {
        preparedSql.append(countFunction.sqlSegment());
    }

    @Override
    public void visit(AsteriskExpr asteriskExpr) {
        preparedSql.append(asteriskExpr.sqlSegment());
    }

    @Override
    public void visit(ConditionChain chain) {
        AutoLinkedList.AutoListIterator<Expression> iterator = chain.listIterator(0);
        while (iterator.hasNext()) {
            Expression expr = iterator.next();

            if (chain.isAndOr(expr) && chain.isAndOr(iterator.getNext())) {
                if (expr instanceof OrExpr) {
                    iterator.removeNext();
                } else {
                    iterator.remove();
                    continue;
                }
            } else if (!chain.isAndOr(expr) && !chain.isAndOr(iterator.getNext()) && iterator.getNext() != null) {
                iterator.addSettle(new AndExpr());
            }
            if (iterator.getNext() == null && chain.isAndOr(expr)) {
                return;
            }
            expr.accept(this);
//            if (expr instanceof Builder) {
//                ((Builder) expr).build(preparedSql);
//            }
            preparedSql.append(StringPool.SPACE);
        }
    }


    @Override
    public void visit(BetweenExpr expr) {
        preparedSql.append(expr.sqlSegment());

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        preparedSql.put(id1, expr.getStart());
        preparedSql.put(id2, expr.getEnd());


        preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id1, expr.getType()).build());
        preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id2, expr.getType()).build());
    }

    @Override
    public void visit(ConstantExpr expr) {

    }

    @Override
    public void visit(EqualExpr expr) {
        commonVisit(expr);
    }

    @Override
    public void visit(GreaterEqualExpr expr) {
        commonVisit(expr);
    }

    @Override
    public void visit(GreaterThanExpr expr) {
        commonVisit(expr);
    }

    @Override
    public void visit(InExpr expr) {
        preparedSql.append(expr.sqlSegment());

        for (Object item : (List<Object>) expr.getValue()) {
            String id = UUID.randomUUID().toString();
            preparedSql.put(id, item);
            preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, expr.getType()).build());
        }
    }

    @Override
    public void visit(LessThanExpr expr) {
        commonVisit(expr);
    }

    @Override
    public void visit(LessEqualExpr expr) {
        commonVisit(expr);
    }

    private void commonVisit(Expression expr) {
        preparedSql.append(expr.sqlSegment());

        String id = UUID.randomUUID().toString();
        preparedSql.put(id, ((AbstractExpr) expr).getValue());

        preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, ((AbstractColumnType) expr).getType()).build());
    }

    @Override
    public void visit(LikeExpr expr) {
        preparedSql.append(expr.sqlSegment());

        String id = UUID.randomUUID().toString();
        preparedSql.put(id, expr.getValue());

        preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, String.class).build());
    }

    @Override
    public void visit(NotEqualExpr expr) {
        commonVisit(expr);
    }

    @Override
    public void visit(NotNullExpr expr) {
        preparedSql.append(expr.sqlSegment());
    }

    @Override
    public void visit(NullExpr expr) {
        preparedSql.append(expr.sqlSegment());
    }

    @Override
    public void visit(OrExpr expr) {
        preparedSql.append(expr.sqlSegment());
    }

    @Override
    public void visit(AndExpr expr) {
        preparedSql.append(expr.sqlSegment());
    }

    @Override
    public void visit(ParenthesisExpr expr) {
        preparedSql.append("(");
        expr.getChain().accept(this);
        preparedSql.append(")");
    }

    @Override
    public void visit(ReferExpr expr) {
        if (expr.isRefDataEmpty() != null && expr.isRefDataEmpty()){
            preparedSql.append(Parser.BOOLEAN_FALSE);
            return;
        }
        visit((InExpr) expr);
    }

    @Override
    public void visit(Table expr) {
        preparedSql.append(expr.getName()).append(StringPool.SPACE);
    }

    @Override
    public void visit(SelectItems selectItems) {
        List<SelectExpr> selects = selectItems.getSelects();
        if (selects.isEmpty()) {
            selects.add(new AsteriskExpr());
        }
        selects.forEach(x->{
            x.accept(this);
            preparedSql.append(StringPool.COMMA);
        });
        preparedSql.removeLast();
    }

    @Override
    public void visit(Order order) {
        List<Order.OrderIItem> orders = order.getOrders();
        if (OneApiUtil.isEmpty(orders)) {
            return;
        }
        preparedSql.append(order.sqlSegment());
        orders.forEach(x->{
            preparedSql.append(x.getColumn().getColumn()).append(StringPool.SPACE).append(x.getOrderEnum().name());
        });
    }
}
