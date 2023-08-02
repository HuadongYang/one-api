package com.yz.oneapi.parser.parser;

import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.utils.OneApiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public final class ParserRegistry {
    private final Map<BiPredicate<Character, String>, TokenParser> parserMap = new HashMap<>();

    private TokenParser defaultParer = new DefaultTokenParser();
    public final static TokenParser emptyParer = new EmptyParser();

    private ParserRegistry() {
        register();
    }

    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final ParserRegistry INSTANCE = new ParserRegistry();
    }

    public static ParserRegistry getInstance() {
        return ParserRegistry.SingletonHolder.INSTANCE;
    }

    private void register() {
        parserMap.put(getPredicate('('), new LeftParenthesisTokenParser());
        parserMap.put(getPredicate(')'), new RightParenthesisTokenParser());
        parserMap.put((character, property) -> character.equals('@') && OneApiUtil.isBlank(property), new KeywordTokenParser());
        parserMap.put((character, property) -> character.equals('@') && OneApiUtil.isNotBlank(property), new ExpressionTokenParser());
        parserMap.put(getPredicate('|'), new OrTokenParser());
        parserMap.put(getPredicate('&'), new AndTokenParser());
    }


    public TokenParser getTokenParser(Character token, String property) {
        for (Map.Entry<BiPredicate<Character, String>, TokenParser> entry : parserMap.entrySet()) {
            BiPredicate<Character, String> k = entry.getKey();
            if (k.test(token, property)) {
                return entry.getValue();
            }
        }
        throw new ParseException("not fount suitable parser " + token);
    }

    public static boolean isSpecialToken(char ch) {
        return ch == '(' || ch == ')' || ch == '@' || ch == '|' || ch == '&';
    }

    public static boolean containSpecialToken(String str) {
        for (char ch : str.toCharArray()) {
            if (isSpecialToken(ch)) {
                return true;
            }
        }
        return false;
    }

    public void execute(List<TokenParser> tokenParsers, String property, Object value, WhereAst selectAst) {
        fix(tokenParsers);
        tokenParsers.forEach(x ->
                x.parse(property, value, selectAst)
        );
    }

    /**
     * @param tokenParsers
     */
    public void fix(List<TokenParser> tokenParsers) {
        boolean hasMainParser = false;
        for (TokenParser tokenParser : tokenParsers) {
            if (tokenParser instanceof KeywordTokenParser || tokenParser instanceof ExpressionTokenParser) {
                hasMainParser = true;
                break;
            }
        }
        if (hasMainParser) {
            //清除占位parser
            tokenParsers.remove(emptyParer);
        } else {
            //用defaultParser替换占位parse
            int i = tokenParsers.indexOf(emptyParer);
            tokenParsers.set(i, defaultParer);
        }
    }

    private BiPredicate<Character, String> getPredicate(Character ch) {
        return (character, property) -> ch.equals(character);
    }


}
