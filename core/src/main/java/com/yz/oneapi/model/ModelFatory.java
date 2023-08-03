package com.yz.oneapi.model;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.model.meta.MetaField;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;
import com.yz.oneapi.utils.JavaTypeUtils;
import com.yz.oneapi.utils.NamingCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelFatory {

    public static List<TableModel> toMultiTableModels(List<MetaField> metaFields, OneApiConfig configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (metaFields == null || metaFields.size() == 0) {
            return null;
        }
        Map<String, List<MetaField>> tableName2Fields = metaFields.stream().collect(Collectors.groupingBy(MetaField::getTableName));
        List<TableModel> results = new ArrayList<>();
        tableName2Fields.forEach((k, v) -> {
            TableModel tableModel = new TableModel();
            tableModel.setTableName(k);
            //tableModel.setTableComment(v.get(0).getTableComment());
            if (configuration.isToCamelCase()) {
                tableModel.setModelName(NamingCase.toCamelCase(k, configuration.getNamingStyle()));
            } else {
                tableModel.setModelName(k);
            }
            List<ColumnModel> columns = new ArrayList<>();
            tableModel.setColumns(columns);
            results.add(tableModel);
            if (v != null && v.size() > 0) {
                v.forEach(x -> {
                    ColumnModel columnModel = getColumnModel(typeHandlerRegistry, x, configuration.isToCamelCase(), configuration.getNamingStyle());
                    columns.add(columnModel);
                });
            }
        });
        return results;
    }

    public static List<TableModelDTO> toMultiTableModelDTO(List<MetaField> metaFields, OneApiConfig configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (metaFields == null || metaFields.size() == 0) {
            return null;
        }
        Map<String, List<MetaField>> tableName2Fields = metaFields.stream().collect(Collectors.groupingBy(MetaField::getTableName));
        List<TableModelDTO> results = new ArrayList<>();
        tableName2Fields.forEach((k, v) -> {
            TableModelDTO tableModel = new TableModelDTO();
            tableModel.setTableName(k);
            tableModel.setTableComment(v.get(0).getTableComment());
            if (configuration.isToCamelCase()) {
                tableModel.setModelName(NamingCase.toCamelCase(k, configuration.getNamingStyle()));
            } else {
                tableModel.setModelName(k);
            }
            List<ColumnModelDTO> columns = new ArrayList<>();
            tableModel.setColumns(columns);
            results.add(tableModel);
            if (v != null && v.size() > 0) {
                v.forEach(x -> {
                    ColumnModelDTO columnModel = getColumnModelDTO(typeHandlerRegistry, x, configuration.isToCamelCase(), configuration.getNamingStyle());
                    columns.add(columnModel);
                });
            }
        });
        return results;
    }

    public static List<ColumnModel> toColumnModels(List<MetaField> metaFields, OneApiConfig configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (metaFields == null || metaFields.size() == 0) {
            return null;
        }
        List<ColumnModel> columns = new ArrayList<>();
        metaFields.forEach(metaField -> {
            ColumnModel columnModel = getColumnModel(typeHandlerRegistry, metaField, configuration.isToCamelCase(), configuration.getNamingStyle());
            columns.add(columnModel);
        });
        return columns;
    }

    private static ColumnModelDTO getColumnModelDTO(TypeHandlerRegistry typeHandlerRegistry, MetaField metaField, boolean toCamelCase, Character namingStyle) {
        ColumnModelDTO columnModel = new ColumnModelDTO();
        columnModel.setColumn(metaField.getColumn());
        if (toCamelCase) {
            columnModel.setAlias(NamingCase.toCamelCase(metaField.getColumn(), namingStyle));
        } else {
            columnModel.setAlias(metaField.getColumn());
        }
        columnModel.setJavaType(JavaTypeUtils.getJavaType(metaField.getSimpleType(), metaField.getDataLength(), metaField.getDataDot(), typeHandlerRegistry));
        columnModel.setPrimary("PRI".equals(metaField.getPrimarys()));
        columnModel.setComment(metaField.getComment());
        columnModel.setTrans(null);
        columnModel.setUniqueCheck(false);
        return columnModel;
    }

    private static ColumnModel getColumnModel(TypeHandlerRegistry typeHandlerRegistry, MetaField metaField, boolean toCamelCase, Character namingStyle) {
        ColumnModel columnModel = new ColumnModel();
        columnModel.setColumn(metaField.getColumn());
        if (toCamelCase) {
            columnModel.setAlias(NamingCase.toCamelCase(metaField.getColumn(), namingStyle));
        } else {
            columnModel.setAlias(metaField.getColumn());
        }
        columnModel.setJavaType(JavaTypeUtils.getJavaType(metaField.getSimpleType(), metaField.getDataLength(), metaField.getDataDot(), typeHandlerRegistry));
        columnModel.setPrimary("PRI".equals(metaField.getPrimarys()));
        columnModel.setComment(metaField.getComment());
        columnModel.setTrans(null);
        columnModel.setUniqueCheck(false);
        return columnModel;
    }
}
