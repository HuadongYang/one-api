package com.yz.oneapi.parser.ast;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.core.Permit;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.mapping.ParameterMap;
import com.yz.oneapi.orm.mapping.ParameterMapping;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.reflection.SystemMetaObject;
import com.yz.oneapi.parser.visitor.SqlAstVisitor;

import java.util.List;

/**
 * todo: parameterMap 由解析器生成
 * todo: builder
 */
public abstract class BaseAst implements SqlAst, Permit {
    protected String sql;
    protected TableModel tableModel;
    protected List<ColumnModel> columnModels;
    protected ParameterMap parameterMap;
    protected Object parameter;
    protected OneApiConfig oneApiConfig;
    protected boolean access = true;

    public BaseAst(TableModel tableModel, OneApiConfig oneApiConfig) {
        this.tableModel = tableModel;
        this.columnModels = tableModel.getColumns();
        this.oneApiConfig = oneApiConfig;
    }

    public BaseAst(OneApiConfig oneApiConfig) {
        this.oneApiConfig = oneApiConfig;
    }

    public BaseAst() {
    }

    public BaseAst(String sql) {
        this.sql = sql;
    }


    @Override
    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<ParameterMapping> parameterMappings = parameterMap.getParameterMappings();
        int que = -1;
        char[] chars = sql.toCharArray();
        MetaObject metaObject = SystemMetaObject.forObject(parameter);
        for (char aChar : chars) {
            if (aChar == '?') {
                que++;
                if (parameterMappings.get(que).getJavaType().getSuperclass() != Number.class) {
                    sb.append("'");
                }
                sb.append(metaObject.getValue(parameterMappings.get(que).getProperty()));
                if (parameterMappings.get(que).getJavaType().getSuperclass() != Number.class) {
                    sb.append("'");
                }
            } else {
                sb.append(aChar);
            }
        }
        return sb.toString();
    }

    @Override
    public void setAccess(boolean access) {
        this.access = access;
    }

    @Override
    public boolean access() {
        return access;
    }

    abstract public BaseAst build();


    public TableModel getTableModel() {
        return tableModel;
    }

    public OneApiConfig getOneApiConfig() {
        return oneApiConfig;
    }
}
