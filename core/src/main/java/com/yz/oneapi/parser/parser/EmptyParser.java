package com.yz.oneapi.parser.parser;

import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.ast.WhereAst;

public class EmptyParser implements TokenParser{

    @Override
    public void parse(String property, Object value, WhereAst selectAst) {
        throw new ParseException("EmptyParser is not valid parser");
    }
}
