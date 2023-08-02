package com.yz.oneapi.parser;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.parser.ast.WhereAst;
import com.yz.oneapi.parser.parser.ParserRegistry;
import com.yz.oneapi.parser.parser.TokenParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * /**
 * * {
 * *    "User":{
 * *      "id(":38710,
 * *      "name@|":"like %le%",
 * *      "createDate@)":"between 2012-01-01 2012-03-01",
 * *      "type@": "in 1,2,3",
 * *      "@order":["name asc", "createDate desc"],
 * *      "@page": 0,
 * *      "@count": 3,
 * *      "userDetails[]":{
 * *          "userId@":"/User/id"
 * *      }
 * *    },
 * *    "moment":{
 * *        "userId@": "currentUserId()"
 * *    },
 * *    "roles":[{
 * *        "name":'sdf',
 * *        "code":"sdf"
 * *    }]
 * * }
 */

public class QueryParser extends AbstractParser {


    public QueryParser(OneApiConfig oneApiConfig) {
        super(oneApiConfig);
    }

    @Override
    public void parse(Map<String, Object> params, WhereAst ast) throws SQLException {
        ParserRegistry parserRegistry = ParserRegistry.getInstance();

        for (String key : params.keySet()) {
            StringBuilder property = new StringBuilder();
            List<TokenParser> tokenParsers = new ArrayList<>();

            for (char ch : key.toCharArray()) {
                if (ParserRegistry.isSpecialToken(ch)) {
                    tokenParsers.add(parserRegistry.getTokenParser(ch, property.toString()));
                }else {
                    property.append(ch);
                }
                if (property.length() == 1) {
                    tokenParsers.add(ParserRegistry.emptyParer);
                }
            }
            parserRegistry.execute(tokenParsers, property.toString(),params.get(key), ast);
        }
    }
}
