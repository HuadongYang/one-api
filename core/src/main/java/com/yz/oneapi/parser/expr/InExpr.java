package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringPool;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 值为逗号分割
 */
public class InExpr extends AbstractExpr {
    private List<Object> sqlVal;
    public static final String SQL_KEY = "IN";

    public InExpr(ColumnModel column, Object value) {
        super(column, value);
    }


    @Override
    void parse() {
        sqlVal = new ArrayList<>();
        if (param instanceof String){
            String[] split = ((String) param).split(StringPool.COMMA);
            for (String str : split) {
                sqlVal.add(AutoConvert.convert(getType(), str));
            }
        }else if (param instanceof Collection) {
            for (Object obj : (List) param) {
                sqlVal.add(AutoConvert.convert(getType(), obj));
            }
        }
    }
    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    @Override
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
        List<Object> value = (List<Object>) getValue();
        if (OneApiUtil.isEmpty(value)) {
            return StringPool.EMPTY;
        }
        StringBuilder s = new StringBuilder(column.getColumn() + CharPool.SPACE + keyword() + CharPool.SPACE + CharPool.LEFT_BRACKET);
        for (int i = 0; i < value.size(); i++) {
            s.append(PLACEHOLDER);
            if (i < value.size() - 1) {
                s.append(StringPool.COMMA);
            }
        }
        s.append(CharPool.RIGHT_BRACKET);
        return s.toString();
    }

    @Override
    public String toString() {
        return StringUtil.formatWith(sqlSegment(), PLACEHOLDER, sqlVal.toArray());
    }
}
