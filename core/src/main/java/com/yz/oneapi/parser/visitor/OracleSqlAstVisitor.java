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
import com.yz.oneapi.utils.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.yz.oneapi.utils.date.format.DatePattern.NORM_DATETIME_PATTERN;

public class OracleSqlAstVisitor extends AbstractSqlAstVisitor {
    private static final String oracleDateFormat = "yyyy-mm-dd hh24:mi:ss";

    public OracleSqlAstVisitor(PreparedSql preparedSql) {
        super(preparedSql);
    }

    @Override
    public void visit(Page page) {
        if (page.getPage() == null || page.getSize() == null) {
            return;
        }
        preparedSql.append(StringUtil.formatWith(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY; ", "?", page.getPage() - 1, page.getSize()));
    }

    @Override
    public void visit(InsertAst insertAst) {
        if (insertAst.getSize() > 1) {
            preparedSql.append("INSERT ALL ");
        }else {
            preparedSql.append("INSERT INTO ");
            //表
            insertAst.getFrom().accept(this);
        }

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
        List<ColumnExpr> columns = new ArrayList<>(insertAst.getValue().keySet());
        String join = Lists.join(columns.stream().map(ColumnExpr::sqlSegment).collect(Collectors.toList()));
        if (insertAst.getSize() > 1) {
            for (int i = 0; i < insertAst.getSize(); i++) {
                ///////////////////////////////////////开始 (column1, column2, column3)//////////////////////////////////////////
                preparedSql.append(" INTO ");
                insertAst.getFrom().accept(this);
                preparedSql.append(" (")
                        .append(join)
                        .append(")");
                ///////////////////////////////////////结束//////////////////////////////////////////
                ///////////////////////////////////////开始 VALUES(?,?,?)//////////////////////////////////////////
                preparedSql.append(" VALUES ").append("(");
                insertPlaceholder(insertAst, columns, i);
                preparedSql.append(") ");
            }
            preparedSql.append("SELECT count(*) FROM dual");
        }else {
            preparedSql.append(" (").append(join).append(" ) ").append(" VALUES ").append("(");
            insertPlaceholder(insertAst, columns, 0);
            preparedSql.append(") ");
        }
    }

    private void insertPlaceholder(InsertAst insertAst, List<ColumnExpr> columns, int i) {
        for (int j = 0; j < columns.size(); j++) {

            //parameterMapping
            String id = UUID.randomUUID().toString();
            Object value = insertAst.getValue().get(columns.get(j))[i];
            if (value instanceof Date || "java.util.Date".equals(columns.get(j).getColumn().getJavaType())) {
                if (StringUtil.equals("date", columns.get(j).getColumn().getFieldType(), true)){
                    preparedSql.append(StringUtil.format("to_date(?, '{}')", oracleDateFormat));
                    if (j < columns.size() - 1) {
                        preparedSql.append(",");
                    }
                    preparedSql.put(id,  DateUtil.format((Date) value, NORM_DATETIME_PATTERN));
                    preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, String.class).build());
                    continue;
                }
            }
            preparedSql.append("?");
            if (j < columns.size() - 1) {
                preparedSql.append(",");
            }
            preparedSql.put(id, value);
            preparedSql.add(new ParameterMapping.Builder(preparedSql.getConfiguration(), id, columns.get(j).getType()).build());
        }
    }

}
