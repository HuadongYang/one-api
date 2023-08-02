package com.yz.oneapi.orm.executor;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.interceptor.SqlCommandType;
import com.yz.oneapi.orm.executor.statement.StatementHandler;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.utils.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {
    public SimpleExecutor(OneApiConfig configuration) {
        super(configuration);
    }

    @Override
    public <E> List<E> query(SqlStatement ms, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        Statement statement = null;
        try {
            OneApiConfig configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(ms, rowBounds, resultHandler);
            statement = prepareStatement(handler);
            long mills = System.currentTimeMillis();
            List<E> query = handler.query(statement);
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql(ms.getSqlAst().toString(), System.currentTimeMillis() - mills, SqlCommandType.SELECT);
            }
            return query;
        } catch (Exception e) {
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql("not execute sql : " + ms.getSqlAst().toString(), 0, SqlCommandType.SELECT);
            }
            throw e;
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public int update(SqlStatement sqlStatement) throws SQLException {
        Statement statement = null;
        try {
            OneApiConfig configuration = sqlStatement.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(sqlStatement, RowBounds.DEFAULT, null);
            statement = prepareStatement(handler);
            long mills = System.currentTimeMillis();
            int update = handler.update(statement);
            String sql = sqlStatement.getSqlAst().toString();
            this.configuration.getInterceptor().printSql(sql, System.currentTimeMillis() - mills, StringUtil.startWithIgnoreCase(sql.trim(), "update") ? SqlCommandType.UPDATE : (StringUtil.startWithIgnoreCase(sql.trim(), "insert") ? SqlCommandType.INSERT : SqlCommandType.DELETE));
            return update;
        } catch (Exception e) {
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql("not execute sql : " + sqlStatement.getSqlAst().toString(), 0, SqlCommandType.SELECT);
            }
            throw e;
        } finally {
            closeStatement(statement);
        }
    }

    private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        stmt = handler.prepare(connection, 10000);
        handler.parameterize(stmt);
        return stmt;
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
