package com.yz.oneapi.parser;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

import java.util.ArrayList;
import java.util.List;

public class Order implements Expression {

    private List<OrderIItem> orders;

    public Order() {
        orders = new ArrayList<>();
    }

    public List<OrderIItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderIItem> orders) {
        this.orders = orders;
    }

    public void addOrder(ColumnModel column, OrderEnum orderEnum) {
        orders.add(new OrderIItem(column, orderEnum == null ? OrderEnum.ASC : orderEnum));
    }

    public void addOrder(ColumnModel column) {
        orders.add(new OrderIItem(column, OrderEnum.ASC));
    }

    @Override
    public String sqlSegment() {
        return " ORDER BY ";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    public static class OrderIItem {
        private ColumnModel column;
        private OrderEnum orderEnum;

        public OrderIItem(ColumnModel column, OrderEnum orderEnum) {
            this.column = column;
            this.orderEnum = orderEnum;
        }

        public ColumnModel getColumn() {
            return column;
        }

        public void setColumn(ColumnModel column) {
            this.column = column;
        }

        public OrderEnum getOrderEnum() {
            return orderEnum;
        }

        public void setOrderEnum(OrderEnum orderEnum) {
            this.orderEnum = orderEnum;
        }
    }
}
