package com.yz.oneapi.parser;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.mapping.ParameterMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparedSql {
    OneApiConfig configuration;
    private StringBuilder sql;
    private List<ParameterMapping> parameterMappings;

    private Map<String, Object> params;

    public PreparedSql(OneApiConfig configuration) {
        this.configuration = configuration;
        sql = new StringBuilder();
        parameterMappings = new ArrayList<>();
        params = new HashMap<>();
    }

    public PreparedSql append(String sqlSegment) {
        sql.append(sqlSegment);
        return this;
    }

    public void removeLast() {
        sql.deleteCharAt(sql.length() - 1);
    }

    public PreparedSql put(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public PreparedSql add(ParameterMapping parameterMapping) {
        parameterMappings.add(parameterMapping);
        return this;
    }

    public StringBuilder getSql() {
        return sql;
    }


    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public OneApiConfig getConfiguration() {
        return configuration;
    }
}
