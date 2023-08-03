package com.yz.oneapi.model.meta;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.core.DbType;
import com.yz.oneapi.orm.executor.SimpleExecutor;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ParameterMapBuilder;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.parser.ast.SelectAst;
import com.yz.oneapi.utils.OneApiUtil;

import java.sql.SQLException;
import java.util.*;

public class MetaRepository {

    private static final String ORACLE_SCHEMA_FIELD = "SELECT t.TABLE_NAME AS \"tableName\", t.COLUMN_NAME AS \"column\", act.COMMENTS AS \"comment\", t.DATA_TYPE AS \"type\",t.DATA_SCALE as \"dataDot\", CASE WHEN t.DATA_PRECISION >= 0 THEN DATA_PRECISION ELSE t.DATA_LENGTH END AS \"dataLength\", t.NULLABLE AS \"dataIsEmpty\", DATA_TYPE AS \"simpleType\", CASE WHEN ( SELECT count( 1 ) FROM user_constraints con, user_cons_columns col WHERE con.constraint_name = col.constraint_name AND con.constraint_type = 'P' AND con.OWNER = ? AND col.column_name = t.column_name AND con.table_name = t.table_name ) > 0 THEN 'PRI' ELSE '' END AS \"primarys\" FROM all_tab_columns t LEFT JOIN all_col_comments act ON t.OWNER = act.OWNER AND t.TABLE_NAME = act.TABLE_NAME AND t.COLUMN_NAME = act.COLUMN_NAME WHERE t.OWNER = ?";
    private static final String ORACLE_TABLE_FIELD = "SELECT t.TABLE_NAME AS \"tableName\", t.COLUMN_NAME AS \"column\", act.COMMENTS AS \"comment\", t.DATA_TYPE AS \"type\",t.DATA_SCALE as \"dataDot\", CASE WHEN t.DATA_PRECISION >= 0 THEN DATA_PRECISION ELSE t.DATA_LENGTH END AS \"dataLength\", t.NULLABLE AS \"dataIsEmpty\", DATA_TYPE AS \"simpleType\", CASE WHEN ( SELECT count( 1 ) FROM user_constraints con, user_cons_columns col WHERE con.constraint_name = col.constraint_name AND con.constraint_type = 'P' AND con.OWNER = ? AND col.column_name = t.column_name AND con.table_name = t.table_name ) > 0 THEN 'PRI' ELSE '' END AS \"primarys\" FROM all_tab_columns t LEFT JOIN all_col_comments act ON t.OWNER = act.OWNER AND t.TABLE_NAME = act.TABLE_NAME AND t.COLUMN_NAME = act.COLUMN_NAME WHERE t.OWNER = ? AND t.TABLE_NAME = ?";
    private static final String MYSQL_SCHEMA_FIELD = "SELECT TABLE_NAME AS tableName, COLUMN_NAME AS `column`, COLUMN_COMMENT AS `comment`, COLUMN_TYPE AS type, COLUMN_KEY AS primarys, CHARACTER_MAXIMUM_LENGTH AS dataLength, NUMERIC_PRECISION AS intLength, COLUMN_DEFAULT AS dataDefaultValue, NUMERIC_SCALE AS dataDot, IS_NULLABLE AS dataIsEmpty, DATA_TYPE AS simpleType FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? ORDER BY TABLE_NAME, ORDINAL_POSITION ASC";

    private static final String MYSQL_TABLE_FIELD = "SELECT TABLE_NAME AS tableName,  COLUMN_NAME AS `column`, COLUMN_COMMENT AS `comment`, COLUMN_TYPE AS type, COLUMN_KEY AS primarys, CHARACTER_MAXIMUM_LENGTH AS dataLength, NUMERIC_PRECISION AS intLength, COLUMN_DEFAULT AS dataDefaultValue, NUMERIC_SCALE AS dataDot, IS_NULLABLE AS dataIsEmpty, DATA_TYPE AS simpleType FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY TABLE_NAME, ORDINAL_POSITION ASC";

    private static final Map<DbType, String[]> dbType2Sql = new HashMap<>();

    static {
        dbType2Sql.put(DbType.MYSQL, new String[]{MYSQL_SCHEMA_FIELD, MYSQL_TABLE_FIELD});
        dbType2Sql.put(DbType.ORACLE, new String[]{ORACLE_SCHEMA_FIELD, ORACLE_TABLE_FIELD});
    }

    private OneApiConfig configuration;

    public MetaRepository(OneApiConfig configuration) {
        this.configuration = configuration;
    }

    public List<MetaField> getMetaFields(String schema, DbType dbType) throws SQLException {
        if (!dbType2Sql.containsKey(dbType)) {
            throw new OneApiException("not support database type : {}", dbType.getDb());
        }

        Map<String, Object> schemaParams = getSchemaParams(schema, dbType);
        ParameterMap parameterMap = new ParameterMapBuilder("schema-meta", configuration).build(schemaParams);

        SelectAst selectAst = new SelectAst(dbType2Sql.get(dbType)[0]);
        selectAst.setParameterMap(parameterMap);
        selectAst.setParameter(schemaParams);

        List<ResultMap> resultMaps = new ArrayList<>();

        ResultMap.Builder builder = new ResultMap.Builder("INFORMATION_SCHEMA", Map.class, new ArrayList<>(), configuration);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, configuration, selectAst);

        SimpleExecutor executor = configuration.newExecutor();
        List<Map<String, Object>> query = executor.query(sqlStatement, new RowBounds(), null);
        return toMetaField(query, dbType);
    }

    private List<MetaField> toMetaField(List<Map<String, Object>> query, DbType dbType) {
        if (OneApiUtil.isEmpty(query)) {
            return null;
        }
        List<MetaField> results = new ArrayList<>();
        query.forEach((map) -> {
            MetaField metaField = new MetaField();
            String tableName = (String) map.get("tableName");
            metaField.setTableName(dbType == DbType.MYSQL ? tableName : tableName.toLowerCase());
            metaField.setTableComment((String)map.get("tableComment"));
            String column = (String) map.get("column");
            metaField.setColumn(dbType == DbType.MYSQL ? column : column.toLowerCase());
            metaField.setComment((String)map.get("comment"));
            metaField.setType((String)map.get("type"));
            metaField.setSimpleType((String)map.get("simpleType"));
            metaField.setPrimarys((String)map.get("primarys"));
            metaField.setDataLength(map.get("dataLength") == null ? 0: Integer.parseInt(String.valueOf(map.get("dataLength"))));
            metaField.setIntLength(map.get("intLength") == null ? 0: Integer.parseInt(String.valueOf(map.get("intLength"))));
            metaField.setDataDefaultValue((String)map.get("dataDefaultValue"));
            metaField.setDataDot(map.get("dataDot") == null ? 0: Integer.parseInt(String.valueOf(map.get("dataDot"))));
            metaField.setDataIsEmpty((String)map.get("dataIsEmpty"));
            results.add(metaField);
        });
        return results;
    }

    private Map<String, Object> getSchemaParams(String schema, DbType dbType) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        if (dbType == DbType.ORACLE) {
            params.put("owner", schema);
            params.put("schema", schema);
        } else {
            params.put("schema", schema);
        }
        return params;
    }

    private Map<String, Object> getTableParams(String schema, String tableName, DbType dbType) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        if (dbType == DbType.ORACLE) {
            params.put("owner", schema);
            params.put("schema", schema);
            params.put("tableName", tableName.toUpperCase());
        } else {
            params.put("schema", schema);
            params.put("tableName", tableName);
        }
        return params;
    }


    public List<MetaField> getMetaFields(String schema, String tableName, DbType dbType) throws SQLException {
        if (!dbType2Sql.containsKey(dbType)) {
            throw new OneApiException("not support database type : {}", dbType.getDb());
        }

        Map<String, Object> tableParams = getTableParams(schema, tableName, dbType);
        ParameterMap parameterMap = new ParameterMapBuilder("table-meta", configuration).build(tableParams);

        SelectAst selectAst = new SelectAst(dbType2Sql.get(dbType)[1]);
        selectAst.setParameterMap(parameterMap);
        selectAst.setParameter(tableParams);

        List<ResultMap> resultMaps = new ArrayList<>();

        ResultMap.Builder builder = new ResultMap.Builder("INFORMATION_SCHEMA", Map.class, new ArrayList<>(), configuration);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, configuration, selectAst);

        SimpleExecutor executor = configuration.newExecutor();
        List<Map<String, Object>> query = executor.query(sqlStatement, new RowBounds(), null);
        return toMetaField(query, dbType);
    }
}
