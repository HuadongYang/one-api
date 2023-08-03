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
        Resource resource = null;
        try {
            OneApiConfig configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(ms, rowBounds, resultHandler);
            resource = prepareStatement(handler);
            long mills = System.currentTimeMillis();
            List<E> query = handler.query(resource.getStmt());
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql(ms.getSqlAst().toString(), System.currentTimeMillis() - mills, SqlCommandType.SELECT);
            }
            return query;
        } catch (Exception e) {
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql("execute error sql : " + ms.getSqlAst().toString(), 0, SqlCommandType.SELECT);
            }
            throw e;
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    @Override
    public int update(SqlStatement sqlStatement) throws SQLException {
        Resource resource = null;
        try {
            OneApiConfig configuration = sqlStatement.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(sqlStatement, RowBounds.DEFAULT, null);
            resource = prepareStatement(handler);
            long mills = System.currentTimeMillis();
            int update = handler.update(resource.getStmt());
            String sql = sqlStatement.getSqlAst().toString();
            this.configuration.getInterceptor().printSql(sql, System.currentTimeMillis() - mills, StringUtil.startWithIgnoreCase(sql.trim(), "update") ? SqlCommandType.UPDATE : (StringUtil.startWithIgnoreCase(sql.trim(), "insert") ? SqlCommandType.INSERT : SqlCommandType.DELETE));
            return update;
        } catch (Exception e) {
            if (this.configuration.getInterceptor() != null) {
                this.configuration.getInterceptor().printSql("execute error sql : " + sqlStatement.getSqlAst().toString(), 0, SqlCommandType.SELECT);
            }
            throw e;
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    private Resource prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        stmt = handler.prepare(connection, 10000);
        handler.parameterize(stmt);
        return new Resource(stmt, connection);
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

    private static class Resource {
        private Statement stmt;
        private Connection connection;

        public Resource(Statement stmt, Connection connection) {
            this.stmt = stmt;
            this.connection = connection;
        }

        public void close() throws SQLException {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();

            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }

        public Statement getStmt() {
            return stmt;
        }

        public void setStmt(Statement stmt) {
            this.stmt = stmt;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }
    }
}
