package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.parser.ast.SqlAst;

import java.util.List;

/**
 * 代表一个方法
 * resultMaps： 方法的出参映射
 * parameterMap： 方法的入参映射
 * dmlEntity: 方法要执行是sql实体
 */
public class SqlStatement {
    private String id;
    private List<ResultMap> resultMaps;
    protected final OneApiConfig configuration;
    /**
     * sqp parameter parameterMap
     */
    private SqlAst sqlAst;
    public SqlStatement(String id, List<ResultMap> resultMaps, OneApiConfig configuration, SqlAst sqlAst) {
        this.id = id;
        this.resultMaps = resultMaps;
        this.configuration = configuration;
        this.sqlAst = sqlAst;
    }


    public String getSql(){
        return this.sqlAst.getSql();
    }

    public String getId() {
        return id;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public OneApiConfig getConfiguration() {
        return configuration;
    }

    public ParameterMap getParameterMap() {
        return sqlAst.getParameterMap();
    }

    public SqlAst getSqlAst() {
        return sqlAst;
    }
}
