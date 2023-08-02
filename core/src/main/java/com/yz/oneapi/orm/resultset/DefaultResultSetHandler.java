package com.yz.oneapi.orm.resultset;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.executor.ErrorContext;
import com.yz.oneapi.orm.executor.ExecutorException;
import com.yz.oneapi.orm.executor.result.DefaultResultContext;
import com.yz.oneapi.orm.executor.result.DefaultResultHandler;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.ResultMapping;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.reflection.MetaClass;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.reflection.ReflectorFactory;
import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.session.ResultContext;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.orm.type.TypeHandler;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DefaultResultSetHandler implements ResultSetHandler {

    private ObjectFactory objectFactory;
    private OneApiConfig configuration;
    private final Map<String, List<UnMappedColumnAutoMapping>> autoMappingsCache = new HashMap<>();
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final ResultHandler<?> resultHandler;
    private final RowBounds rowBounds;
    private final SqlStatement sqlStatement;
    private final ReflectorFactory reflectorFactory;


    public DefaultResultSetHandler(SqlStatement sqlStatement, ResultHandler<?> resultHandler, RowBounds rowBounds) {
        this.configuration = sqlStatement.getConfiguration();
        this.objectFactory = this.configuration.getObjectFactory();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.sqlStatement = sqlStatement;
        this.resultHandler = resultHandler;
        this.rowBounds = rowBounds;
        this.reflectorFactory = configuration.getReflectorFactory();
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        ErrorContext.instance().activity("handling results").object(sqlStatement.getId());

        final List<Object> multipleResults = new ArrayList<>();

        int resultSetCount = 0;
        ResultSetWrapper rsw = getFirstResultSet(stmt);

        List<ResultMap> resultMaps = sqlStatement.getResultMaps();
        int resultMapCount = resultMaps.size();
        validateResultMapsCount(rsw, resultMapCount);
        while (rsw != null && resultMapCount > resultSetCount) {
            ResultMap resultMap = resultMaps.get(resultSetCount);
            handleResultSet(rsw, resultMap, multipleResults, null);
            rsw = getNextResultSet(stmt);
            resultSetCount++;
        }
        return collapseSingleResultList(multipleResults);
    }

    private List<Object> collapseSingleResultList(List<Object> multipleResults) {
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }

    private ResultSetWrapper getNextResultSet(Statement stmt) {
        // Making this method tolerant of bad JDBC drivers
        try {
            if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
                // Crazy Standard JDBC way of determining if there are more results
                if (!(!stmt.getMoreResults() && stmt.getUpdateCount() == -1)) {
                    ResultSet rs = stmt.getResultSet();
                    if (rs == null) {
                        return getNextResultSet(stmt);
                    } else {
                        return new ResultSetWrapper(rs, configuration);
                    }
                }
            }
        } catch (Exception e) {
            // Intentionally ignored.
        }
        return null;
    }

    private void validateResultMapsCount(ResultSetWrapper rsw, int resultMapCount) {
        if (rsw != null && resultMapCount < 1) {
            throw new ExecutorException("A query was run and no Result Maps were found for the Mapped Statement '" + sqlStatement.getId()
                    + "'.  It's likely that neither a Result Type nor a Result Map was specified.");
        }
    }

    private ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            // move forward to get the first resultset in case the driver
            // doesn't return the resultset as the first result (HSQLDB 2.1)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    // no more results. Must be no resultset
                    break;
                }
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults, ResultMapping parentMapping) throws SQLException {
        try {
            if (parentMapping != null) {
                handleRowValues(rsw, resultMap, null, RowBounds.DEFAULT, parentMapping);
            } else {
                if (resultHandler == null) {
                    DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
                    handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
                    multipleResults.add(defaultResultHandler.getResultList());
                } else {
                    handleRowValues(rsw, resultMap, resultHandler, rowBounds, null);
                }
            }
        } finally {
            // issue #228 (close resultsets)
            closeResultSet(rsw.getResultSet());
        }
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        handleRowValuesForSimpleResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
    }

    private void handleRowValuesForSimpleResultMap(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds, ResultMapping parentMapping)
            throws SQLException {
        DefaultResultContext<Object> resultContext = new DefaultResultContext<>();
        ResultSet resultSet = rsw.getResultSet();
        while (shouldProcessMoreRows(resultContext, rowBounds) && !resultSet.isClosed() && resultSet.next()) {
            Object rowValue = getRowValue(rsw, resultMap, null);
            storeObject(resultHandler, resultContext, rowValue, parentMapping, resultSet);
        }
    }

    private void storeObject(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue, ResultMapping parentMapping, ResultSet rs) throws SQLException {
        callResultHandler(resultHandler, resultContext, rowValue);
    }

    @SuppressWarnings("unchecked" /* because ResultHandler<?> is always ResultHandler<Object>*/)
    private void callResultHandler(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue) {
        resultContext.nextResultObject(rowValue);
        ((ResultHandler<Object>) resultHandler).handleResult(resultContext);
    }


    private boolean shouldProcessMoreRows(ResultContext<?> context, RowBounds rowBounds) {
        return !context.isStopped() && context.getResultCount() < rowBounds.getLimit();
    }

    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        Object rowValue = createResultObject(rsw, resultMap);
        if (rowValue != null && !hasTypeHandlerForResultObject(rsw, resultMap.getType())) {
            final MetaObject metaObject = configuration.newMetaObject(rowValue);
            boolean foundValues = false;
            foundValues = applyAutomaticMappings(rsw, resultMap, metaObject, columnPrefix) || foundValues;
            foundValues = applyPropertyMappings(rsw, resultMap, metaObject, columnPrefix) || foundValues;
            rowValue = foundValues ? rowValue : null;
        }
        return rowValue;
    }

    private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        List<UnMappedColumnAutoMapping> autoMapping = createAutomaticMappings(rsw, resultMap, metaObject, columnPrefix);
        boolean foundValues = false;
        if (!autoMapping.isEmpty()) {
            for (UnMappedColumnAutoMapping mapping : autoMapping) {
                final Object value = mapping.typeHandler.getResult(rsw.getResultSet(), mapping.column);
                if (value != null) {
                    foundValues = true;
                }
                if (value != null || configuration.isCallSettersOnNulls()) {
                    // gcode issue #377, call setter on nulls (value is not 'found')
                    metaObject.setValue(mapping.property, value);
                }
            }
        }
        return foundValues;
    }

    private boolean applyPropertyMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
        boolean foundValues = false;
        final List<ResultMapping> propertyMappings = resultMap.getResultMappings();
        for (ResultMapping propertyMapping : propertyMappings) {
            String column = propertyMapping.getColumn();

            if (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) {
                Object value = getPropertyMappingValue(rsw.getResultSet(), propertyMapping);
                // issue #541 make property optional
                final String property = propertyMapping.getProperty();
                if (property == null) {
                    continue;
                }
                if (value != null
                        || configuration.isCallSettersOnNulls() && !metaObject.getSetterType(property).isPrimitive()) {
                    // gcode issue #377, call setter on nulls (value is not 'found')
                    metaObject.setValue(property, value);
                    foundValues = true;
                }
            }
        }
        return foundValues;
    }

    private Object getPropertyMappingValue(ResultSet rs, ResultMapping propertyMapping) throws SQLException {
        final TypeHandler<?> typeHandler = propertyMapping.getTypeHandler();
        return typeHandler.getResult(rs, propertyMapping.getColumn());
    }

    private List<UnMappedColumnAutoMapping> createAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final String mapKey = resultMap.getId();
        List<UnMappedColumnAutoMapping> autoMapping = autoMappingsCache.get(mapKey);
        if (autoMapping == null) {
            autoMapping = new ArrayList<>();
            final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
            for (String columnName : unmappedColumnNames) {
                String propertyName = columnName;
                if (columnPrefix != null && !columnPrefix.isEmpty()) {
                    // When columnPrefix is specified,
                    // ignore columns without the prefix.
                    if (columnName.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
                        propertyName = columnName.substring(columnPrefix.length());
                    } else {
                        continue;
                    }
                }
                final String property = metaObject.findProperty(propertyName, configuration.isToCamelCase(), configuration.getNamingStyle());
                final Class<?> propertyType;
                try {
                    propertyType = Class.forName(rsw.getColumnClass(columnName));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (typeHandlerRegistry.hasTypeHandler(propertyType, rsw.getJdbcType(columnName))) {
                    final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
                    autoMapping.add(new UnMappedColumnAutoMapping(columnName, property, typeHandler, propertyType.isPrimitive()));
                }
            }
            autoMappingsCache.put(mapKey, autoMapping);
        }
        return autoMapping;
    }

    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap)
            throws SQLException {
        final Class<?> resultType = resultMap.getType();
        final MetaClass metaType = MetaClass.forClass(resultType, reflectorFactory);
        if (hasTypeHandlerForResultObject(rsw, resultType)) {
            return createPrimitiveResultObject(rsw, resultMap);
        }
        return objectFactory.create(resultType);
    }

    private boolean hasTypeHandlerForResultObject(ResultSetWrapper rsw, Class<?> resultType) {
        if (rsw.getColumnNames().size() == 1) {
            return typeHandlerRegistry.hasTypeHandler(resultType, rsw.getJdbcType(rsw.getColumnNames().get(0)));
        }
        return typeHandlerRegistry.hasTypeHandler(resultType);
    }
    private Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        final Class<?> resultType = resultMap.getType();
        final String columnName;
        if (!resultMap.getResultMappings().isEmpty()) {
            final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
            final ResultMapping mapping = resultMappingList.get(0);
            columnName = mapping.getColumn();
        } else {
            columnName = rsw.getColumnNames().get(0);
        }
        final TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
        return typeHandler.getResult(rsw.getResultSet(), columnName);
    }


    private static class UnMappedColumnAutoMapping {
        private final String column;
        private final String property;
        private final TypeHandler<?> typeHandler;
        private final boolean primitive;

        public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
            this.column = column;
            this.property = property;
            this.typeHandler = typeHandler;
            this.primitive = primitive;
        }
    }
}
