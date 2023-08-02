package com.yz.oneapi.orm.resultset;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.io.ClassLoaderWrapper;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.ObjectTypeHandler;
import com.yz.oneapi.orm.type.TypeHandler;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultSetWrapper {
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<>();
    private final ResultSet resultSet;

    private final List<String> columnNames = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();
    private final List<JdbcType> jdbcTypes = new ArrayList<>();
    private final List<Class> javaTypes = new ArrayList<>();
    private final Map<String, List<String>> mappedColumnNamesMap = new HashMap<>();
    private final Map<String, List<String>> unMappedColumnNamesMap = new HashMap<>();

    private final ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    public ResultSet getResultSet() {
        return resultSet;
    }

    public ResultSetWrapper(ResultSet rs, OneApiConfig configuration) throws SQLException {
        super();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.resultSet = rs;
        final ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
            jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));

            classNames.add(metaData.getColumnClassName(i));
        }
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
        TypeHandler<?> handler = null;
        Map<Class<?>, TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
        if (columnHandlers == null) {
            columnHandlers = new HashMap<>();
            typeHandlerMap.put(columnName, columnHandlers);
        } else {
            handler = columnHandlers.get(propertyType);
        }
        if (handler == null) {
            JdbcType jdbcType = getJdbcType(columnName);
            handler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
            // Replicate logic of UnknownTypeHandler#resolveTypeHandler
            // See issue #59 comment 10
            if (handler == null) {
                final int index = columnNames.indexOf(columnName);
                final Class<?> javaType = resolveClass(classNames.get(index));
                if (javaType != null && jdbcType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
                } else if (javaType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(javaType);
                } else if (jdbcType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(jdbcType);
                }
            }
            if (handler == null) {
                handler = new ObjectTypeHandler();
            }
            columnHandlers.put(propertyType, handler);
        }
        return handler;
    }

    public List<String> getUnmappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
        List<String> unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap));
        if (unMappedColumnNames == null) {
            loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
            unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap));
        }
        return unMappedColumnNames;
    }


    public List<String> getMappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
        List<String> mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap));
        if (mappedColumnNames == null) {
            loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
            mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap));
        }
        return mappedColumnNames;
    }

    private void loadMappedAndUnmappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
        List<String> mappedColumnNames = new ArrayList<>();
        List<String> unmappedColumnNames = new ArrayList<>();
        final String upperColumnPrefix = columnPrefix == null ? null : columnPrefix.toUpperCase(Locale.ENGLISH);
        final Set<String> mappedColumns = resultMap.getMappedColumns();
        for (String columnName : columnNames) {
            final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
            if (mappedColumns.contains(upperColumnName)) {
                mappedColumnNames.add(upperColumnName);
            } else {
                unmappedColumnNames.add(columnName);
            }
        }
        mappedColumnNamesMap.put(getMapKey(resultMap), mappedColumnNames);
        unMappedColumnNamesMap.put(getMapKey(resultMap), unmappedColumnNames);
    }
    private String getMapKey(ResultMap resultMap) {
        return resultMap.getId();
    }

    private Class<?> resolveClass(String className) {
        try {
            // #699 className could be null
            if (className != null) {

                return classLoaderWrapper.classForName(className);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return null;
    }
    public JdbcType getJdbcType(String columnName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                return jdbcTypes.get(i);
            }
        }
        return null;
    }
    public String getColumnClass(String columnName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                return classNames.get(i);
            }
        }
        return null;
    }

    static class MetaColumn {

    }
}
