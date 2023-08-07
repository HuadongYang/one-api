package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Ordered;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

public class NotEqualExpr extends AbstractExpr implements Ordered {
    private Object sqlVal;
    public static final String SQL_KEY = "<>";
    public static final String SQL_TEMPLATE = SQL_KEY + " ?";
    public NotEqualExpr(ColumnModel column, Object value) {
        super(column, value);
    }

    @Override
    void parse() {
        sqlVal = AutoConvert.convert(getType(),param);
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }


    public Object getValue() {
        return sqlVal;
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String keyword() {
        return SQL_KEY;
    }

    @Override
    public String sqlSegment() {
        return column.getColumn() + CharPool.SPACE + SQL_TEMPLATE;
    }



    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String toString() {
        return StringUtil.formatWith(sqlSegment(), "?", sqlVal);
    }
}

