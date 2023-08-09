package com.yz.oneapi.parser;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

import java.util.ArrayList;
import java.util.List;

public class Order implements Expression {

    private List<OrderItem> orders;

    public Order() {
        orders = new ArrayList<>();
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public void addOrder(ColumnModel column, OrderEnum orderEnum) {
        orders.add(new OrderItem(column, orderEnum == null ? OrderEnum.ASC : orderEnum));
    }

    public void addOrder(ColumnModel column) {
        orders.add(new OrderItem(column, OrderEnum.ASC));
    }

    @Override
    public String sqlSegment() {
        return " ORDER BY ";
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    public static class OrderItem {
        private ColumnModel column;
        private OrderEnum orderEnum;

        public OrderItem(ColumnModel column, OrderEnum orderEnum) {
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
