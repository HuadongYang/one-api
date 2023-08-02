package com.yz.oneapi.parser.expr;

import com.yz.oneapi.executor.ResultWrapperHolder;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * "userId@":"/User/id"
 */
public class ReferExpr extends InExpr {

    private List<Object> val;
    public static final String SQL_KEY = "/";

    public ReferExpr(ColumnModel column, Object value) {
        super(column, value);
    }

    @Override
    void parse() {
        val = new ArrayList<>();
        if (param instanceof String) {
            List<String> split = StringUtil.splitTrim(((String) param).trim(), SQL_KEY.charAt(0));
            if (split.size() < 2) {
                throw new ParseException("refer format error");
            }
            ResultWrapperHolder result = ResultWrapperHolder.getResult();
            String rightProperty = split.get(split.size() - 1);
            String rightModel = split.get(split.size() - 2);
            result.addJoin(column.getModelName(), new ResultWrapperHolder.JoinOn(column.getAlias(), rightProperty));

            List<Object> referData = result.getData(rightModel);
            if (OneApiUtil.isEmpty(referData)) {
                return;
            }
            referData.forEach((obj) -> {
                Object referVal = ((Map<String, Object>) obj).get(rightProperty);
                val.add(AutoConvert.convert(getType(), referVal));
            });

        }
    }
    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }

    @Override
    public Object getValue() {
        return val;
    }
}
