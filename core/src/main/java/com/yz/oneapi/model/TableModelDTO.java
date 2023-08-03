package com.yz.oneapi.model;

import com.yz.oneapi.utils.OneApiUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableModelDTO implements Serializable, Cloneable{
    private String tableName;
    private String modelName;

    private String tableComment;
    private Boolean access;

    private Boolean warming;

    private Boolean customId;

    private List<ColumnModelDTO> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public List<ColumnModelDTO> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModelDTO> columns) {
        this.columns = columns;
    }

    public Boolean getWarming() {
        return warming;
    }

    public void setWarming(Boolean warming) {
        this.warming = warming;
    }

    public Boolean getCustomId() {
        return customId;
    }

    public void setCustomId(Boolean customId) {
        this.customId = customId;
    }

    @Override
    protected TableModelDTO clone() throws CloneNotSupportedException {
        TableModelDTO clone = (TableModelDTO) super.clone();
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
        return clone;
    }
}
