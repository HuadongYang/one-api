package com.yz.oneapi.parser.ast;

import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.parser.expr.Expression;

/**
 * 语法树，类似mapper xml
 */
public interface SqlAst extends Ast, Expression {

    //获取dml
    String getId();

    String getSql();

    ParameterMap getParameterMap();

    Object getParameter();

    SqlStatement getSqlStatement();
}
