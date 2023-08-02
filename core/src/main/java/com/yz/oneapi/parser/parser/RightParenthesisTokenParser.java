package com.yz.oneapi.parser.parser;

import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.ParenthesisExpr;


public class RightParenthesisTokenParser implements TokenParser {

    /**
     * tail     最后一个可链接的node，最后一个指的是可继续向下链接的最后一个；两个圆括号内的节点肯定不是最后一个
     *
     * @param property
     * @param
     * @return
     */
    public void parse(String property, Object value, WhereAst selectAst) {
        ConditionChain linkableChain = selectAst.getWhere().getLinkableChain();
        if (linkableChain.getParent() instanceof ParenthesisExpr) {
            ((ParenthesisExpr) linkableChain.getParent()).close();
        } else {
            throw new ParseException("token ')' parse exception");
        }
    }

}
