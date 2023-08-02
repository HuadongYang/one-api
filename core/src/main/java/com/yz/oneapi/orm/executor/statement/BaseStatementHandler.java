package com.yz.oneapi.orm.executor.statement;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.executor.ErrorContext;
import com.yz.oneapi.orm.executor.ExecutorException;
import com.yz.oneapi.orm.executor.parameter.ParameterHandler;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.resultset.ResultSetHandler;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 执行者
 * 方法 sql 结果处理器 参数处理器
 */
public abstract class BaseStatementHandler implements StatementHandler {


    protected final SqlStatement sqlStatement;
    protected final OneApiConfig configuration;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;


    public BaseStatementHandler(SqlStatement sqlStatement, RowBounds rowBounds, ResultHandler resultHandler) {
        this.sqlStatement = sqlStatement;
        this.configuration = sqlStatement.getConfiguration();
        this.resultSetHandler = configuration.newResultSetHandler(sqlStatement, rowBounds, resultHandler);
        this.parameterHandler = configuration.newParameterHandler(sqlStatement);
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        ErrorContext.instance().sql(sqlStatement.getSqlAst().getSql());
        Statement statement = null;
        try {
            statement = instantiateStatement(connection);
            setStatementTimeout(statement, transactionTimeout);
            setFetchSize(statement);
            return statement;
        } catch (SQLException e) {
            closeStatement(statement);
            throw e;
        } catch (Exception e) {
            closeStatement(statement);
            throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    protected void setStatementTimeout(Statement stmt, Integer transactionTimeout) throws SQLException {
        Integer queryTimeout = null;
        if (configuration.getDefaultStatementTimeout() != null) {
            queryTimeout = configuration.getDefaultStatementTimeout();
        }
        if (queryTimeout != null) {
            stmt.setQueryTimeout(queryTimeout);
        }
        if (queryTimeout == null || queryTimeout == 0 || transactionTimeout < queryTimeout) {
            stmt.setQueryTimeout(transactionTimeout);
        }
    }

    protected void setFetchSize(Statement stmt) throws SQLException {
        Integer defaultFetchSize = configuration.getDefaultFetchSize();
        if (defaultFetchSize != null) {
            stmt.setFetchSize(defaultFetchSize);
        }
    }

    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            //ignore
        }
    }
}
