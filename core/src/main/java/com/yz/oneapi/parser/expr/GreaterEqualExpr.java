package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.parser.Ordered;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

import java.util.UUID;

public class GreaterEqualExpr extends AbstractExpr implements Ordered {
    public static final String SQL_KEY = ">=";
    public static final String SQL_TEMPLATE = SQL_KEY + " ?";
    private Object sqlVal;
    public GreaterEqualExpr(ColumnModel column, Object value) {
        super(column, value);
    }

    @Override
    void parse() {
        sqlVal = AutoConvert.convert(getType(), param);
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
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

    public Object getValue() {
        return sqlVal;
    }
}

