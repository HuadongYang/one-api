package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringPool;
import com.yz.oneapi.utils.StringUtil;

import java.util.UUID;

public class LikeExpr extends AbstractExpr{
    private String sqlVal;

    public static final String SQL_KEY = "LIKE";
    public static final String SQL_TEMPLATE = SQL_KEY + " " + PLACEHOLDER;

    public LikeExpr(ColumnModel column, Object value) {
        super(column, value);
    }


    @Override
    void parse() {
        String substring = (String) param;
        if (!substring.startsWith(StringPool.PERCENT) && substring.endsWith(StringPool.PERCENT)) {
            substring = StringPool.PERCENT + substring + StringPool.PERCENT;
        }
        this.sqlVal = substring;
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
    public String getValue() {
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
    public String toString(){
        return StringUtil.format(SQL_TEMPLATE, sqlVal);
    }

}
