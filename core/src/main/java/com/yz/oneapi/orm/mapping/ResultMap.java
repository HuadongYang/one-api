package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.model.ColumnModel;

import java.util.*;

/**
 * 出参映射
 */
public class ResultMap {
    private OneApiConfig configuration;
    private Class<?> type;
    private String id;
    private List<ResultMapping> resultMappings;
    private Set<String> mappedColumns;
    private Set<String> mappedProperties;

    private ResultMap() {
    }

    public static class Builder {

        private final ResultMap resultMap = new ResultMap();

        public Builder(String id, Class<?> type, List<ResultMapping> resultMappings, OneApiConfig configuration) {
            this(id, configuration);
            resultMap.type = type;
            resultMap.resultMappings = resultMappings == null ? new ArrayList<>() : resultMappings;
        }

        public Builder(String id, List<ColumnModel> columnModels, OneApiConfig configuration) throws ClassNotFoundException {
            this(id, configuration);
            resultMap.id = id;
            resultMap.type = Map.class;
            resultMap.resultMappings = new ArrayList<>();
            for (ColumnModel columnModel : columnModels) {
                ResultMapping resultMapping = new ResultMapping.Builder(configuration, columnModel.getAlias(), columnModel.getColumn(), Class.forName(columnModel.getJavaType())).build();
                resultMap.resultMappings.add(resultMapping);
            }
        }

        public Builder(String id, OneApiConfig configuration) {
            resultMap.id = id;
            resultMap.configuration = configuration;
        }



        public ResultMap build() {
            if (resultMap.id == null) {
                throw new IllegalArgumentException("ResultMaps must have an id");
            }
            resultMap.mappedColumns = new HashSet<>();
            resultMap.mappedProperties = new HashSet<>();
            for (ResultMapping resultMapping : resultMap.resultMappings) {
                final String column = resultMapping.getColumn();
                if (column != null) {
                    resultMap.mappedColumns.add(column.toUpperCase(Locale.ENGLISH));
                }
                final String property = resultMapping.getProperty();
                if (property != null) {
                    resultMap.mappedProperties.add(property);
                }

            }

            // lock down collections
            resultMap.resultMappings = Collections.unmodifiableList(resultMap.resultMappings);
            resultMap.mappedColumns = Collections.unmodifiableSet(resultMap.mappedColumns);
            return resultMap;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public void setResultMappings(List<ResultMapping> resultMappings) {
        this.resultMappings = resultMappings;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public void setMappedColumns(Set<String> mappedColumns) {
        this.mappedColumns = mappedColumns;
    }
}
