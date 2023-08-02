package com.yz.oneapi.parser.parser;

import com.yz.oneapi.parser.ast.WhereAst;

public interface TokenParser {
    void parse(String property, Object value, WhereAst selectAst);
}
