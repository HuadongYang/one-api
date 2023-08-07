package com.yz.oneapi.executor;

import com.yz.oneapi.parser.expr.Builder;
import com.yz.oneapi.parser.expr.Expression;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;
import com.yz.oneapi.utils.OneApiUtil;

import java.util.List;

public class Page implements Builder, Expression {

    private Long total;
    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页
     */
    private Long page;

    private List<Object> data;

    public boolean isPage(){
        return size != null && page != null;
    }

    public boolean notEmpty(){
        return OneApiUtil.isNotEmpty(data);
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
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
