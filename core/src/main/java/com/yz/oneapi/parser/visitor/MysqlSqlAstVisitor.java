package com.yz.oneapi.parser.visitor;

import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.executor.Page;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.parser.PreparedSql;
import com.yz.oneapi.parser.ast.InsertAst;
import com.yz.oneapi.parser.expr.select.ColumnExpr;
import com.yz.oneapi.utils.Lists;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AutoConvert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MysqlSqlAstVisitor extends AbstractSqlAstVisitor {

    public MysqlSqlAstVisitor(PreparedSql preparedSql) {
        super(preparedSql);
    }

    @Override
    public void visit(Page page) {
        if (page.getPage() == null || page.getSize() == null) {
            return;
        }
        preparedSql.append(StringUtil.formatWith(" LIMIT ?, ? ", "?", page.getPage() - 1, page.getSize()));
    }

    @Override
    public void visit(InsertAst insertAst) {
        preparedSql.append("INSERT INTO ");
        //表
        insertAst.getFrom().accept(this);

        //补充主键
        TableModel tableModel = insertAst.getTableModel();
        ColumnModel primaryColumn = tableModel.getPrimaryColumn();
        if (primaryColumn == null) {
            throw new OneApiException("not found primary key from {}", tableModel.getTableName());
        }
        ColumnExpr primary = new ColumnExpr(primaryColumn);
        insertAst.getValue().computeIfAbsent(primary, k -> new Object[insertAst.getSize()]);
        for (int i = 0; i < insertAst.getSize(); i++) {
            insertAst.getValue().get(primary)[i] = AutoConvert.convert(primary.getType(), insertAst.getOneApiConfig().getId(tableModel.getModelName()));
        }
        ///////////////////////////////////////开始 (column1, column2, column3)//////////////////////////////////////////
        preparedSql.append("(");
        List<ColumnExpr> columns = new ArrayList<>(insertAst.getValue().keySet());
        preparedSql.append(Lists.join(columns.stream().map(ColumnExpr::sqlSegment).collect(Collectors.toList())));
        preparedSql.append(")");
        ///////////////////////////////////////结束//////////////////////////////////////////
        preparedSql.append(" VALUES ");
        ///////////////////////////////////////开始 (?,?,?),(?,?,?)//////////////////////////////////////////
        for (int i = 0; i < insertAst.getSize(); i++) {
            preparedSql.append("(");

            for (int j = 0; j < columns.size(); j++) {
                preparedSql.append("?");
                if (j < columns.size() - 1) {
                    preparedSql.append(",");
                }
                //parameterMapping
                String id = UUID.randomUUID().toString();
                preparedSql.put(id, insertAst.getValue().get(columns.get(j))[i]);
                preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, columns.get(j).getType()).build());
            }
            preparedSql.append(")");
            if (i < insertAst.getSize() - 1) {
                preparedSql.append(",");
            }
        }
    }


}
