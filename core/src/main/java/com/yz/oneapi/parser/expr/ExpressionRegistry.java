package com.yz.oneapi.parser.expr;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExpressionRegistry {

    private Map<Predicate<String>, ExprFunctions> expressionMap;

    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final ExpressionRegistry INSTANCE = new ExpressionRegistry();
    }

    public static ExpressionRegistry getInstance() {
        return ExpressionRegistry.SingletonHolder.INSTANCE;
    }

    public ExpressionRegistry() {
        register();
    }

    public Expression getExpr(String value, ColumnModel columnModel) {
        for (Predicate<String> predicate : expressionMap.keySet()) {
            if (predicate.test(value)) {
                ExprFunctions exprFunctions = expressionMap.get(predicate);
                return exprFunctions.getNewFuc().apply(columnModel, exprFunctions.getValueFunc().apply(value));
            }
        }
        return new EqualExpr(columnModel, value);
    }

    public void register() {
        expressionMap = new LinkedHashMap<>();
        expressionMap.put(normalTest(BetweenExpr.SQL_KEY), new ExprFunctions(BetweenExpr::new, valueSubString(BetweenExpr.SQL_KEY)));
        expressionMap.put(normalTest(LikeExpr.SQL_KEY), new ExprFunctions(LikeExpr::new, valueSubString(LikeExpr.SQL_KEY)));
        expressionMap.put(compareTest(EqualExpr.SQL_KEY), new ExprFunctions(EqualExpr::new, valueSubString(EqualExpr.SQL_KEY)));
        expressionMap.put(compareTest(GreaterEqualExpr.SQL_KEY), new ExprFunctions(GreaterEqualExpr::new, valueSubString(GreaterEqualExpr.SQL_KEY)));
        expressionMap.put(compareTest(LessEqualExpr.SQL_KEY), new ExprFunctions(LessEqualExpr::new, valueSubString(LessEqualExpr.SQL_KEY)));
        expressionMap.put(compareTest(GreaterThanExpr.SQL_KEY), new ExprFunctions(GreaterThanExpr::new, valueSubString(GreaterThanExpr.SQL_KEY)));
        expressionMap.put(compareTest(LessThanExpr.SQL_KEY), new ExprFunctions(LessThanExpr::new, valueSubString(LessThanExpr.SQL_KEY)));
        expressionMap.put(normalTest(InExpr.SQL_KEY), new ExprFunctions(InExpr::new, valueSubString(InExpr.SQL_KEY)));
        expressionMap.put(referTest(ReferExpr.SQL_KEY), new ExprFunctions(ReferExpr::new, valueNull(ReferExpr.SQL_KEY)));
        expressionMap.put(notEqualTest(NotEqualExpr.SQL_KEY), new ExprFunctions(NotEqualExpr::new, valueSubString(NotEqualExpr.SQL_KEY)));
        expressionMap.put(notNullTest(NotNullExpr.SQL_KEY), new ExprFunctions(NotNullExpr::new, valueNull(NotNullExpr.SQL_KEY)));
        expressionMap.put(nullTest(NullExpr.SQL_KEY), new ExprFunctions(NullExpr::new, valueNull(NullExpr.SQL_KEY)));
        expressionMap.put(normalTest(EqualExpr.SQL_KEY), new ExprFunctions(EqualExpr::new, valueSubString(EqualExpr.SQL_KEY)));
    }


    public static Function<Object, Object> valueSubString(String keyword){
        return (val)-> {
            if (val instanceof String) {
                val = ((String)val).trim().substring(keyword.length()).trim();
            }
            return val;
        };
    }
    public static Function<Object, Object> valueNull(String keyword){
        return (val) -> val;
    }
    public static Predicate<String> normalTest(String keyword) {
        return (value) -> StringUtil.startWithIgnoreCase(value, keyword + CharPool.SPACE);
    }



    public static Predicate<String> compareTest(String keyword) {
        return (value) -> StringUtil.startWithIgnoreCase(value, keyword);
    }


    public static Predicate<String> referTest(String keyword) {
        return (value) -> value.startsWith(keyword);
    }

    public static Predicate<String> notEqualTest(String keyword) {
        return (value) -> StringUtil.startWithIgnoreCase(value, keyword) || StringUtil.startWithIgnoreCase(value, "!=");
    }

    public static Predicate<String> notNullTest(String keyword) {
        return (value) -> {
            String[] split = value.split("\\s");
            return split.length == 3 && StringUtil.equals(split[0], "is", true) && StringUtil.equals(split[1], "not", true) && StringUtil.equals(split[2], "null", true);
        };
    }

    public static Predicate<String> nullTest(String keyword) {
        return (value) -> {
            String[] split = value.split("\\s");
            return split.length == 2 && StringUtil.equals(split[0], "is", true) && StringUtil.equals(split[1], "null", true);
        };
    }

    private static class ExprFunctions {
        private BiFunction<ColumnModel, Object, Expression> newFuc;
        private Function<Object, Object> valueFunc;

        public ExprFunctions(BiFunction<ColumnModel, Object, Expression> newFuc, Function<Object, Object> valueFunc) {
            this.newFuc = newFuc;
            this.valueFunc = valueFunc;
        }

        public BiFunction<ColumnModel, Object, Expression> getNewFuc() {
            return newFuc;
        }

        public void setNewFuc(BiFunction<ColumnModel, Object, Expression> newFuc) {
            this.newFuc = newFuc;
        }

        public Function<Object, Object> getValueFunc() {
            return valueFunc;
        }

        public void setValueFunc(Function<Object, Object> valueFunc) {
            this.valueFunc = valueFunc;
        }
    }

}
