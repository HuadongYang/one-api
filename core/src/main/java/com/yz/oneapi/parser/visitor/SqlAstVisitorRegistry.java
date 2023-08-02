package com.yz.oneapi.parser.visitor;

import com.yz.oneapi.core.DbType;
import com.yz.oneapi.parser.PreparedSql;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SqlAstVisitorRegistry {

    Map<DbType, Function<PreparedSql, SqlAstVisitor>> map = new HashMap<>();

    private SqlAstVisitorRegistry() {
        register();
    }

    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final SqlAstVisitorRegistry INSTANCE = new SqlAstVisitorRegistry();
    }

    public static SqlAstVisitorRegistry getInstance() {
        return SqlAstVisitorRegistry.SingletonHolder.INSTANCE;
    }

    private void register(){
        map.put(DbType.MYSQL, MysqlSqlAstVisitor::new);
        map.put(DbType.ORACLE, OracleSqlAstVisitor::new);
    }

    public SqlAstVisitor getSqlAstVisitor(DbType dbType, PreparedSql preparedSql){
        if (map.containsKey(dbType)) {
            return map.get(dbType).apply(preparedSql);
        }
        return null;
    }

}
