package com.yz.oneapi.parser.ast;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.Page;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.ResultMapping;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.parser.Order;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.chain.ConditionChain;
import com.yz.oneapi.parser.expr.ConstantExpr;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.expr.function.CountFunction;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.parser.visitor.SqlAstVisitorRegistry;
import com.yz.oneapi.utils.ClassUtil;
import com.yz.oneapi.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {
 * "User":{
 * "id":38710,
 * "name@|":"like %le%",
 * "createDate@":"between 2012-01-01 2012-03-01",
 * "type@": "in 1,2,3",
 * "@order":["name asc", "createDate desc"],
 * "@page": 0,
 * "@count": 3,
 * "userDetails[]":{
 * "userId@":"/User/id"
 * }
 * },
 * "moment":{
 * "userId@": "currentUserId()"
 * },
 * "roles":[{
 * "name":'sdf',
 * "code":"sdf"
 * }]
 * }
 */
public class SelectAst extends BaseAst implements WhereAst {

    private SelectItems selects;
    private Table from;
    private ConditionChain where;
    private Page page;
    private Order order;
    private List<ResultMapping> resultMappings;


    public SelectAst(TableModel tableModel, OneApiConfig oneApiConfig) {
        super(tableModel, oneApiConfig);
        init();
    }


    public SelectAst(String sql) {
        super(sql);
        init();
    }

    public void init() {
        selects = new SelectItems();
        resultMappings = new ArrayList<>();
        if (tableModel != null) {
            from = new Table(tableModel.getTableName(), tableModel.getModelName());
            buildResultMapping();
        }
        where = new ConditionChain();
        order = new Order();
        page = new Page();
    }

    /**
     * 添加where条件
     * @param expressions 要添加的where条件
     */
    public SelectAst addWhere(Expression expressions) {
        if (expressions == null) {
            return this;
        }
        ConditionChain linkableChain = where.getLinkableChain();
        linkableChain.addLast(expressions);
        return this;
    }
    /**
     * 排除查询的字段
     * @param exclusionCols 排除查询的字段
     */
    public void exclusionColumns(String... exclusionCols){
        selects.exclusionColumns(tableModel, exclusionCols);
    }

    public SelectAst totalAst() {
        SelectAst totalAst = new SelectAst(tableModel, oneApiConfig);
        totalAst.selects = new SelectItems(Lists.newArrayList(new CountFunction(new ConstantExpr(1))));
        totalAst.from = this.from;
        totalAst.where = this.where;
        return totalAst;
    }


    public SelectAst build() {
        PreparedSql preparedSql = new PreparedSql(oneApiConfig);
        SqlAstVisitor sqlAstVisitor = SqlAstVisitorRegistry.getInstance().getSqlAstVisitor(oneApiConfig.getDbType(), preparedSql);

        preparedSql.append("SELECT ");
        //查询字段
        selects.accept(sqlAstVisitor);

        //表
        preparedSql.append(" FROM ");
        from.accept(sqlAstVisitor);
        //where条件
        if (where.size() > 0) {
            preparedSql.append(" WHERE ");
            where.accept(sqlAstVisitor);
        }
        order.accept(sqlAstVisitor);
        //limit
        page.accept(sqlAstVisitor);

        setSql(preparedSql.getSql().toString());
        setParameterMap(new ParameterMap.Builder(tableModel.getTableName() + getId(), preparedSql.getParameterMappings()).build());
        setParameter(preparedSql.getParams());

        return this;
    }

    @Override
    public String sqlSegment() {
        return null;
    }

    @Override
    public void accept(SqlAstVisitor v) {

    }

    @Override
    public SqlStatement getSqlStatement(){
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder(tableModel.getTableName() + getId(), Map.class, this.resultMappings, oneApiConfig);
        resultMaps.add(builder.build());
        return new SqlStatement(tableModel.getTableName() + getId(), resultMaps, oneApiConfig, this);
    }

    private void buildResultMapping(){
        this.resultMappings.clear();
        List<ColumnModel> columns = tableModel.getColumns();
        columns.forEach(column -> {
            this.resultMappings.add(new ResultMapping.Builder(oneApiConfig, column.getAlias(), column.getColumn(), ClassUtil.loadClass(column.getJavaType())).build());
        });

    }

    public SqlStatement getTotalSqlStatement(){
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder(tableModel.getTableName()+ getId(), Long.class, null, oneApiConfig);
        resultMaps.add(builder.build());
        return new SqlStatement(tableModel.getTableName() + getId(), resultMaps, oneApiConfig, this);
    }

    @Override
    public String getId() {
        return "-queryList";
    }


    public ColumnModel getColumnByProperty(String property) {
        return this.columnModels.stream().filter(x -> x.getAlias().equals(property)).findFirst().orElse(null);
    }


    public ConditionChain getWhere() {
        return where;
    }

    public Page getPage() {
        return page;
    }
    public Order getOrder() {
        return order;
    }

    public SelectItems getSelects() {
        return selects;
    }

    public Table getFrom() {
        return from;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public TableModel getTableModel(){
        return tableModel;
    }

}
