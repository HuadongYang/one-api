package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParameterMapBuilder {
    protected List<ParameterMapping> parameterMappings;
    protected ParameterMap parameterMap;

    protected OneApiConfig configuration;

    public ParameterMapBuilder(String id, OneApiConfig configuration) {
        this.configuration = configuration;
        parameterMappings = new ArrayList<>();
        parameterMap = new ParameterMap.Builder(id, parameterMappings).build();
    }

    public ParameterMap build(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return parameterMap;
        }
        params.forEach((k, v) -> {
            parameterMappings.add(new ParameterMapping.Builder(configuration, k, v.getClass()).build());
        });
        return parameterMap;
    }



}
