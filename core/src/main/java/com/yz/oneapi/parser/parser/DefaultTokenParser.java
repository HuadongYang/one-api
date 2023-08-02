package com.yz.oneapi.parser.parser;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.EqualExpr;
import com.yz.oneapi.parser.expr.Expression;

public class DefaultTokenParser implements TokenParser {



    @Override
    public void parse(String property, Object value, WhereAst selectAst) {
        ConditionChain linkableChain = selectAst.getWhere().getLinkableChain();
        ColumnModel column = selectAst.getColumnByProperty(property);
        Expression exp = new EqualExpr(column, value);
        linkableChain.addLast(exp);
    }
}
