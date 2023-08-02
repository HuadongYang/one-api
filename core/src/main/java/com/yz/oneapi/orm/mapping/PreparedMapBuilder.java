package com.yz.oneapi.orm.mapping;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.utils.Lists;
import com.yz.oneapi.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PreparedMapBuilder extends ParameterMapBuilder {

    private String sql;
    private final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private final String SELECT_SQL_TEMPLATE = "SELECT * FROM %s";

    public PreparedMapBuilder(String id, OneApiConfig configuration) {
        super(id, configuration);
    }

    public <T> PreparedMap buildInsertMapping(String tableName, T t) {
        Field[] fields = t.getClass().getDeclaredFields();

        List<String> columns = new ArrayList<>();
        MetaObject metaObject = configuration.newMetaObject(t);
        for (Field field : fields) {
            String property = metaObject.findProperty(field.getName(), configuration.isToCamelCase(), configuration.getNamingStyle());
            Object value = metaObject.getValue(property);
            if (value != null) {
                columns.add(field.getName());
                parameterMappings.add(new ParameterMapping.Builder(configuration, property, field.getType()).build());
            }
        }
        if (columns.size() == 0) {
            throw new MappingException("insert entity not has valuable value");
        }
        String fieldNames = StringUtil.collectionToDelimitedString(columns, ",");
        String placeholder = StringUtil.collectionToDelimitedString(Lists.fill("?", columns.size()), ",");
        this.sql = String.format(INSERT_SQL_TEMPLATE, tableName, fieldNames, placeholder);
        return new PreparedMap(sql, parameterMap);
    }

    public PreparedMap buildSelectMapping(String tableName) {
        this.sql = String.format(SELECT_SQL_TEMPLATE, tableName);
        return new PreparedMap(sql, null);
    }

    public class PreparedMap {
        private String sql;
        protected ParameterMap parameterMap;

        public PreparedMap(String sql, ParameterMap parameterMap) {
            this.sql = sql;
            this.parameterMap = parameterMap;
        }

        public String getSql() {
            return sql;
        }

        public ParameterMap getParameterMap() {
            return parameterMap;
        }
    }

    public String getSql() {
        return sql;
    }

    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    /**
     * 此方法要求T中字段的顺序与sql中参数的顺序是一致的，且全部应用
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> ParameterMap build(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            parameterMappings.add(new ParameterMapping.Builder(configuration, field.getName(), field.getType()).build());
        }
        return parameterMap;
    }

    public <T> ParameterMap build(Field... fields) {
        for (Field field : fields) {
            parameterMappings.add(new ParameterMapping.Builder(configuration, field.getName(), field.getType()).build());
        }
        return parameterMap;
    }
}
