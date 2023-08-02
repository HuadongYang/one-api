package com.yz.oneapi.parser.parser;

import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.OrExpr;

public class OrTokenParser implements TokenParser{

    @Override
    public void parse(String property, Object value, WhereAst selectAst) {
        ConditionChain linkableChain = selectAst.getWhere().getLinkableChain();
        linkableChain.addLast(new OrExpr());
    }
}
