package com.yz.oneapi.parser.ast;

import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.expr.select.AsteriskExpr;
import com.yz.oneapi.parser.expr.select.ColumnExpr;
import com.yz.oneapi.parser.expr.select.SelectExpr;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectItems implements Expression {
    private List<SelectExpr> selects;

    public SelectItems() {
        selects = new ArrayList<>();
    }

    public SelectItems(List<SelectExpr> selects) {
        this.selects = selects;
    }

    public void exclusionColumns(TableModel tableModel, String... exclusionCols){
        if ((selects.size() == 1 && selects.get(0) instanceof AsteriskExpr) || selects.isEmpty()){
            selects.clear();
            List<ColumnModel> columns = tableModel.getColumns();
            columns.forEach(col->{
                for (String exclusionCol : exclusionCols) {
                    if (col.getAlias().equals(exclusionCol)) {
                        return;
                    }
                }
                selects.add(new ColumnExpr(col));
            });
        }else if (selects.stream().allMatch(x->x instanceof ColumnExpr)) {
            Iterator<SelectExpr> iterator = selects.iterator();
            while (iterator.hasNext()) {
                for (String exclusionCol : exclusionCols) {
                    if (((ColumnExpr)iterator.next()).getColumn().getAlias().equals(exclusionCol)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void addSelect(SelectExpr selectExpr){
        selects.add(selectExpr);
    }

    public List<SelectExpr> getSelects() {
        return selects;
    }

    public void setSelects(List<SelectExpr> selects) {
        this.selects = selects;
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
