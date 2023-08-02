package com.yz.oneapi.model;

import java.io.Serializable;

public class ColumnModel implements Serializable, Cloneable {
    private String column;
    private String alias;
    private String javaType;
    private Boolean primary;
    private String comment;
    private Boolean uniqueCheck;
    private String trans;
    private String modelName;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getUniqueCheck() {
        return uniqueCheck;
    }

    public void setUniqueCheck(Boolean uniqueCheck) {
        this.uniqueCheck = uniqueCheck;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    protected ColumnModel clone() throws CloneNotSupportedException {
        return (ColumnModel) super.clone();
    }
}
