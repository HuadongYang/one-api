package com.yz.oneapi.interceptor;

import java.util.function.Supplier;

public class ColumnFill {
    private ColumnIdx source;

    //更新还是新增
    private SqlCommandType command;

    private Supplier<Object> fillRule;

    public ColumnFill() {
    }

    public ColumnFill(ColumnIdx source, SqlCommandType command, Supplier<Object> fillRule) {
        this.source = source;
        this.command = command;
        this.fillRule = fillRule;
    }

    public boolean isTable(String tableAlias) {
        return tableAlias.matches(source.getTableAliasRegex());
    }

    public ColumnIdx getSource() {
        return source;
    }

    public void setSource(ColumnIdx source) {
        this.source = source;
    }

    public Supplier<Object> getFillRule() {
        return fillRule;
    }

    public void setFillRule(Supplier<Object> fillRule) {
        this.fillRule = fillRule;
    }

    public SqlCommandType getCommand() {
        return command;
    }

    public void setCommand(SqlCommandType command) {
        this.command = command;
    }
}
