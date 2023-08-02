package com.yz.oneapi.parser.ast;

import com.yz.oneapi.executor.Page;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.chain.ConditionChain;

public interface WhereAst extends Ast{
    ConditionChain getWhere();

    ColumnModel getColumnByProperty(String property);

    Page getPage();
    Order getOrder();
}
