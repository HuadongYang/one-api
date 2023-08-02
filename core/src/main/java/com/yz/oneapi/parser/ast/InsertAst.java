package com.yz.oneapi.parser.ast;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.interceptor.ColumnFill;
import com.yz.oneapi.interceptor.SqlCommandType;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.expr.select.ColumnExpr;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.parser.visitor.SqlAstVisitorRegistry;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.Lists;
import com.yz.oneapi.utils.convert.AutoConvert;

import java.util.*;
import java.util.stream.Collectors;

public class InsertAst extends BaseAst {

    private Table from;
    //string为property Object[]为值数组，长度为要插入的数量
    private Map<ColumnExpr, Object[]> value;
    private int size;


    public InsertAst(String sql) {
        super(sql);
    }

    public InsertAst(TableModel tableModel, OneApiConfig oneApiConfig, int size) {
        super(tableModel, oneApiConfig);
        value = new HashMap<>();
        this.size = size;
        init();
    }

    public InsertAst(OneApiConfig oneApiConfig) {
        super(oneApiConfig);
    }

    private void init() {
        if (tableModel != null) {
            from = new Table(tableModel.getTableName(), tableModel.getModelName());
        }
    }

    public InsertAst append(Map<String, Object> param, int index) {
        param.forEach((property, value) -> {
            ColumnModel columnByProperty = tableModel.getColumnByProperty(property);
            if (columnByProperty == null) {
                throw new OneApiException("no fount the column : {}", property);
            }
            ColumnExpr key = new ColumnExpr(columnByProperty);
            this.value.computeIfAbsent(key, k -> new Object[size]);
            this.value.get(key)[index] = AutoConvert.convert(key.getType(), value);
        });
        return this;
    }

    //填充
    private void autoFill() {
        if (oneApiConfig.getInterceptor() == null || OneApiUtil.isEmpty(oneApiConfig.getInterceptor().autoFill())) {
            return;
        }
        List<ColumnFill> columnFills = oneApiConfig.getInterceptor().autoFill();
        List<ColumnFill> theFills = columnFills.stream().filter(x -> x.isTable(from.getAlias()) && x.getCommand() == SqlCommandType.INSERT).collect(Collectors.toList());
        if (OneApiUtil.isEmpty(theFills)) {
            return;
        }
        List<String> existColumns = this.value.keySet().stream().map(ColumnExpr::getColumnAlias).collect(Collectors.toList());
        theFills.forEach(theFill -> {
            if (existColumns.contains(theFill.getSource().getColumnAlias())) {
                return;
            }
            ColumnModel column = tableModel.getColumnByProperty(theFill.getSource().getColumnAlias());
            if (column == null) {
                return;
            }
            ColumnExpr key = new ColumnExpr(column);
            this.value.computeIfAbsent(key, k -> new Object[size]);
            Object fillVal =  AutoConvert.convert(key.getType(), theFill.getFillRule().get());
            Arrays.fill(this.value.get(key), fillVal);
        });
    }

    public InsertAst build() {
        autoFill();
        PreparedSql preparedSql = new PreparedSql(oneApiConfig);
        SqlAstVisitor sqlAstVisitor = SqlAstVisitorRegistry.getInstance().getSqlAstVisitor(oneApiConfig.getDbType(), preparedSql);
        accept(sqlAstVisitor);
        ///////////////////////////////////////结束//////////////////////////////////////////
        setSql(preparedSql.getSql().toString());
        setParameterMap(new ParameterMap.Builder(tableModel.getTableName() + getId(), preparedSql.getParameterMappings()).build());
        setParameter(preparedSql.getParams());
        return this;
    }

    @Override
    public SqlStatement getSqlStatement() {
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder(tableModel.getTableName() + getId(), Integer.class, null, oneApiConfig);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement(tableModel.getTableName() + getId(), resultMaps, oneApiConfig, this);
        return sqlStatement;
    }

    @Override
    public String getId() {
        return "-insert";
    }

    public Map<ColumnExpr, Object[]> getValue() {
        return value;
    }

    public Table getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }


    @Override
    public String sqlSegment() {
        return null;
    }

    @Override
    public void accept(SqlAstVisitor v) {
        v.visit(this);
    }
}
