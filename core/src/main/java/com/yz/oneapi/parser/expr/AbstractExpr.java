package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;

import java.util.UUID;

public abstract class AbstractExpr extends AbstractColumnType implements Expression, Builder {
    protected String id;
    protected Object param;


    public AbstractExpr(ColumnModel column, Object param) {
        super(column);
        this.id = UUID.randomUUID().toString();
        this.param = param;
        parse();
    }
    public AbstractExpr(ColumnModel column){
        super(column);
        parse();
    }



    public Object getValue() {
        return null;
    }


    abstract String keyword();
    abstract String id();

    abstract void parse();



}
