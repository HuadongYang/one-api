package com.yz.oneapi.parser;

import com.yz.oneapi.parser.ast.WhereAst;

import java.sql.SQLException;
import java.util.Map;

/**
 * {
 *    "User[]":{
 *      "id":38710
 *      "name@":"like %le%",
 *      "createDate@":"between 2012-01-01 2012-03-01",
 *      "type@": "in 1,2,3",
 *      "@order":["name asc", "createDate desc"],
 *      "@page": 0,
 *      "@count": 3,
 *      "userDetails[]":{
 *          "userId@":"/User/id"    //User的id：3870
 *      }
 *    },
 *    "moment[]":{
 *        "userId@": "currentUserId()"        //当前用户id
 *    }
 * }
 */
public interface Parser {

    String METHOD_MARK = "[]";
    void parse(Map<String, Object> params, WhereAst selectAst) throws SQLException;
}
