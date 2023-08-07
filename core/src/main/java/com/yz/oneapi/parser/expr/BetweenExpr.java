package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Ordered;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

public class BetweenExpr extends AbstractExpr implements Ordered {
    private Object start;
    private Object end;

    public static final String SQL_KEY = "BETWEEN";
    public static final String SQL_TEMPLATE = SQL_KEY + " ? and ?";

    public BetweenExpr(ColumnModel column, Object value) {
        super(column, value);

    }
    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
    //"between 2012-01-01 11-11-11,2012-03-01"
    @Override
    void parse() {
        String[] valsArr = ((String) param).split(SPLIT);
        if (valsArr.length != 2) {
            throw new ParseException(SQL_KEY + " parse error : value length != 2");
        }
        start = AutoConvert.convert(getType(), valsArr[0]);
        end = AutoConvert.convert(getType(), valsArr[1]);
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
    public String toString() {
        return StringUtil.formatWith(sqlSegment(), "?", start, end);
    }


    @Override
    public int getOrder() {
        return 0;
    }

    public Object getStart() {
        return start;
    }

    public Object getEnd() {
        return end;
    }
}
