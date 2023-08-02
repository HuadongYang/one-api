package com.yz.oneapi.parser.parser;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.EqualExpr;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.expr.ExpressionRegistry;

public class ExpressionTokenParser implements TokenParser {


    @Override
    public void parse(String property, Object value, WhereAst selectAst) {
        ConditionChain linkableChain = selectAst.getWhere().getLinkableChain();
        ColumnModel column = selectAst.getColumnByProperty(property);
        if (column == null) {
            throw new ParseException("cannot found column for property : {}", property);
        }

        Expression exp;
        if (value instanceof String) {
            exp = ExpressionRegistry.getInstance().getExpr(((String) value).trim(), column);
        } else {
            exp = new EqualExpr(column, value);
        }
        linkableChain.addLast(exp);
    }
}
