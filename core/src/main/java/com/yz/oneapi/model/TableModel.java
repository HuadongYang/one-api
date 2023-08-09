package com.yz.oneapi.model;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.utils.OneApiUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableModel implements Serializable, Cloneable {
    private String tableName;
    private String modelName;

    private String tableComment;

    private List<ColumnModel> columns;
    private Map<String, ColumnModel> property2Column;
    private Map<String, ColumnModel> name2Column;
    private ColumnModel primary;



    public ColumnModel getColumnByProperty(String property) {
        return property2Column.get(property);
    }

    public ColumnModel getColumnByName(String columnName) {
        ColumnModel columnModel = name2Column.get(columnName);
        if (columnModel == null) {
            throw new OneApiException("no column for column : {}", columnName);
        }
        return columnModel;
    }

    public ColumnModel getPrimaryColumn() {
        return this.columns.stream().filter(ColumnModel::getPrimary).findFirst().orElse(null);
    }

    public boolean isPrimaryByProperty(String property) {
        return primary.getAlias().equals(property);
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
        refresh();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void refresh() {
        this.property2Column = columns.stream().collect(Collectors.toMap(ColumnModel::getAlias, Function.identity()));
        this.name2Column = columns.stream().collect(Collectors.toMap(ColumnModel::getColumn, Function.identity()));
        this.primary = this.columns.stream().filter(ColumnModel::getPrimary).findFirst().orElse(null);
    }

    @Override
    protected TableModel clone() throws CloneNotSupportedException {
        TableModel clone = (TableModel) super.clone();
        clone.columns = new ArrayList<>();
        if (OneApiUtil.isNotEmpty(this.columns)){
            this.columns.forEach(x-> {
                try {
                    clone.columns.add(x.clone());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (this.primary != null) {
            clone.primary = this.primary.clone();
        }
        clone.refresh();
        return clone;
    }


}
