package com.yz.oneapi.model.meta;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MetaField {
    private String tableName;
    private String tableComment;
    //字段名称
    private String column;
    private String comment;
    //varchar(10)
    private String type;
    //varchar
    private String simpleType;
    //PRI UNI
    private String primarys;
    private Integer dataLength;
    private Integer intLength;
    private String dataDefaultValue;
    private Integer dataDot;
    private String dataIsEmpty;

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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(String simpleType) {
        this.simpleType = simpleType;
    }

    public String getPrimarys() {
        return primarys;
    }

    public void setPrimarys(String primarys) {
        this.primarys = primarys;
    }


    public String getDataDefaultValue() {
        return dataDefaultValue;
    }

    public void setDataDefaultValue(String dataDefaultValue) {
        this.dataDefaultValue = dataDefaultValue;
    }


    public String getDataIsEmpty() {
        return dataIsEmpty;
    }

    public void setDataIsEmpty(String dataIsEmpty) {
        this.dataIsEmpty = dataIsEmpty;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public Integer getIntLength() {
        return intLength;
    }

    public void setIntLength(Integer intLength) {
        this.intLength = intLength;
    }

    public Integer getDataDot() {
        return dataDot;
    }

    public void setDataDot(Integer dataDot) {
        this.dataDot = dataDot;
    }
}
