/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yz.oneapi.orm.executor.parameter;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.TypeException;
import com.yz.oneapi.orm.type.TypeHandler;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;
import com.yz.oneapi.parser.ast.SqlAst;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DefaultParameterHandler implements ParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final SqlStatement sqlStatement;
    private final Object parameterObject;
    private final OneApiConfig configuration;
    private List<ParameterMapping> parameterMappings;
    private MetaObject parameterMeta;

    public DefaultParameterHandler(SqlStatement sqlStatement) {
        this.sqlStatement = sqlStatement;
        this.configuration = sqlStatement.getConfiguration();
        this.typeHandlerRegistry = sqlStatement.getConfiguration().getTypeHandlerRegistry();
        SqlAst sqlAst = sqlStatement.getSqlAst();
        this.parameterObject = sqlAst.getParameter();
        if (sqlAst.getParameterMap() != null) {
            this.parameterMappings = sqlAst.getParameterMap().getParameterMappings();
        }
        this.parameterMeta = configuration.newMetaObject(sqlAst.getParameter());
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        //ErrorContext.instance().activity("setting parameters").object(sqlStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = this.parameterMappings;
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    value = parameterMeta.getValue(propertyName);
                }
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = configuration.getJdbcTypeForNull();
                }
                try {
                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                } catch (TypeException | SQLException e) {
                    throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                }
            }
        }
    }

}
