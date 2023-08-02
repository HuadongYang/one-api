package com.yz.oneapi.parser.ast;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.Page;
import com.yz.oneapi.interceptor.ColumnFill;
import com.yz.oneapi.interceptor.SqlCommandType;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.EqualExpr;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.parser.visitor.SqlAstVisitorRegistry;
import com.yz.oneapi.utils.OneApiUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateAst extends BaseAst implements WhereAst {

    private Table from;
    //todo：封装
    private List<EqualExpr> value;
    private ConditionChain where;

    public UpdateAst(TableModel tableModel, OneApiConfig oneApiConfig) {
        super(tableModel, oneApiConfig);
        init();
    }

    private void init() {
        if (tableModel != null) {
            from = new Table(tableModel.getTableName(), tableModel.getModelName());
        }
        value = new ArrayList<>();
        where = new ConditionChain();
    }

    public UpdateAst build() {
        autoFill();
        PreparedSql preparedSql = new PreparedSql(oneApiConfig);
        SqlAstVisitor sqlAstVisitor = SqlAstVisitorRegistry.getInstance().getSqlAstVisitor(oneApiConfig.getDbType(), preparedSql);
        preparedSql.append("UPDATE ");
        //表
        from.accept(sqlAstVisitor);
        preparedSql.append(" SET ");
        for (int i = 0; i < value.size(); i++) {
            value.get(i).accept(sqlAstVisitor);
            if (i < value.size() - 1) {
                preparedSql.append(",");
            }
        }
        preparedSql.append(" WHERE ");
        where.accept(sqlAstVisitor);
        setSql(preparedSql.getSql().toString());
        setParameterMap(new ParameterMap.Builder(tableModel.getTableName() + getId(), preparedSql.getParameterMappings()).build());
        setParameter(preparedSql.getParams());
        return this;
    }

    //填充
    private void autoFill() {
        if (oneApiConfig.getInterceptor() == null || OneApiUtil.isEmpty(oneApiConfig.getInterceptor().autoFill())) {
            return;
        }
        List<ColumnFill> columnFills = oneApiConfig.getInterceptor().autoFill();
        List<ColumnFill> theFills = columnFills.stream().filter(x -> x.isTable(from.getAlias()) && x.getCommand() == SqlCommandType.UPDATE).collect(Collectors.toList());
        if (OneApiUtil.isEmpty(theFills)) {
            return;
        }
        List<String> existColumns = this.value.stream().map(x -> x.getColumn().getAlias()).collect(Collectors.toList());
        theFills.forEach(theFill -> {
            if (existColumns.contains(theFill.getSource().getColumnAlias())) {
                return;
            }
            ColumnModel column = tableModel.getColumnByProperty(theFill.getSource().getColumnAlias());
            if (column == null) {
                return;
            }
            this.value.add(new EqualExpr(column, theFill.getFillRule().get()));
        });
    }

    public void append(ColumnModel column, Object value) {
        this.value.add(new EqualExpr(column, value));
    }


    @Override
    public String getId() {
        return "-update";
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
    public ConditionChain getWhere() {
        return where;
    }

    @Override
    public ColumnModel getColumnByProperty(String property) {
        return this.columnModels.stream().filter(x -> x.getAlias().equals(property)).findFirst().orElse(null);
    }

    @Override
    public Page getPage() {
        throw new ParseException();
    }

    @Override
    public Order getOrder() {
        throw new ParseException();
    }

    @Override
    public String sqlSegment() {
        return null;
    }

    @Override
    public void accept(SqlAstVisitor v) {

    }
}
