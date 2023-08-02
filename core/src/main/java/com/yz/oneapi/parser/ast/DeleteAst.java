package com.yz.oneapi.parser.ast;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.Page;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.ParseException;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.parser.visitor.SqlAstVisitorRegistry;

import java.util.ArrayList;
import java.util.List;

public class DeleteAst extends BaseAst implements WhereAst {

    private Table from;
    private ConditionChain where;

    public DeleteAst(TableModel tableModel, OneApiConfig oneApiConfig) {
        super(tableModel, oneApiConfig);
        init();
    }

    private void init() {
        from = new Table(tableModel.getTableName(), tableModel.getModelName());
        where = new ConditionChain();
    }

    @Override
    public String getId() {
        return "-delete";
    }

    public DeleteAst build() {
        PreparedSql preparedSql = new PreparedSql(oneApiConfig);
        SqlAstVisitor sqlAstVisitor = SqlAstVisitorRegistry.getInstance().getSqlAstVisitor(oneApiConfig.getDbType(), preparedSql);
        preparedSql.append("DELETE FROM ");
        //表
        from.accept(sqlAstVisitor);
        //where
        preparedSql.append(" WHERE ");
        where.accept(sqlAstVisitor);
        setSql(preparedSql.getSql().toString());
        setParameterMap(new ParameterMap.Builder(tableModel.getTableName() + getId(), preparedSql.getParameterMappings()).build());
        setParameter(preparedSql.getParams());
        return this;
    }

    @Override
    public SqlStatement getSqlStatement() {
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder(tableModel.getTableName()+ getId(), Integer.class, null, oneApiConfig);
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
