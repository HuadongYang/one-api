package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.TypeHandler;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;

public class ResultMapping {
    private OneApiConfig configuration;
    private String column;
    private String property;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    ResultMapping() {
    }

    public static class Builder {
        private final ResultMapping resultMapping = new ResultMapping();

        public Builder(OneApiConfig configuration, String property, String column, TypeHandler<?> typeHandler) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.typeHandler = typeHandler;
        }

        public Builder(OneApiConfig configuration, String property, String column, Class<?> javaType) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.javaType = javaType;
        }
        public Builder(OneApiConfig configuration, String property) {
            resultMapping.configuration = configuration;
            resultMapping.property = property;
        }



        public Builder javaType(Class<?> javaType) {
            resultMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            resultMapping.jdbcType = jdbcType;
            return this;
        }



        public Builder typeHandler(TypeHandler<?> typeHandler) {
            resultMapping.typeHandler = typeHandler;
            return this;
        }



        public ResultMapping build() {
            // lock down collections
            resolveTypeHandler();
            return resultMapping;
        }

        private void resolveTypeHandler() {
            if (resultMapping.typeHandler == null && resultMapping.javaType != null) {
                OneApiConfig configuration = resultMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                resultMapping.typeHandler = typeHandlerRegistry.getTypeHandler(resultMapping.javaType, resultMapping.jdbcType);
            }
        }

        public Builder column(String column) {
            resultMapping.column = column;
            return this;
        }
    }


    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public OneApiConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OneApiConfig configuration) {
        this.configuration = configuration;
    }
}
