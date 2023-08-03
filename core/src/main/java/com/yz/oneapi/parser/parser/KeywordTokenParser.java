package com.yz.oneapi.parser.parser;

import com.yz.oneapi.executor.Page;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.OrderEnum;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.utils.Convert;
import com.yz.oneapi.utils.StringPool;
import com.yz.oneapi.utils.StringUtil;

import java.util.Collection;
import java.util.List;

public class KeywordTokenParser implements TokenParser {

    @Override
    public void parse(String property, Object value, WhereAst selectAst) {
        Order order = selectAst.getOrder();
        Page page = selectAst.getPage();
        if ("order".equals(property)) {
            if (value instanceof Collection) {
                List<String> orders = (List<String>) value;
                for (String item : orders) {
                    String[] split = item.trim().split("\\w");
                    if (split.length == 1) {
                        ColumnModel column = selectAst.getColumnByProperty(split[0]);
                        if (column == null) {
                            throw new ParseException("cannot found column for property : {}", property);
                        }
                        order.addOrder(column);
                    } else if (split.length == 2) {
                        ColumnModel column = selectAst.getColumnByProperty(split[0]);
                        if (column == null) {
                            throw new ParseException("cannot found column for property : {}", property);
                        }
                        order.addOrder(column, OrderEnum.getByCode(split[1]));
                    }
                }
            } else if (value instanceof String) {
                String[] properties = ((String) value).split(StringPool.COMMA);
                for (String prop : properties) {
                    String[] splits = prop.trim().split("\\s+");
                    ColumnModel column = selectAst.getColumnByProperty(splits[0]);
                    if (column != null) {
                        if (splits.length > 1 && OrderEnum.getByCode(splits[1]) != null){
                            order.addOrder(column, OrderEnum.getByCode(splits[1]));
                        }else {
                            order.addOrder(column);
                        }
                    }
                }
            } else {
                throw new ParseException("KeywordParser: unexpected keyword: " + property);
            }
        } else if ("page".equals(property)) {
            page.setPage(Convert.convertInt(value));
        } else if ("size".equals(property)) {
            page.setSize(Convert.convertInt(value));
        } else {
            throw new ParseException("KeywordParser: unexpected keyword: " + property);
        }
    }
}
