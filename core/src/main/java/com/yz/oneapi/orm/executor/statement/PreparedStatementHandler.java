package com.yz.oneapi.orm.executor.statement;

import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PreparedStatementHandler extends BaseStatementHandler {



    public PreparedStatementHandler(SqlStatement sqlStatement, RowBounds rowBounds, ResultHandler resultHandler) {
        super(sqlStatement, rowBounds, resultHandler);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = sqlStatement.getSql();
        return connection.prepareStatement(sql);
    }


    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ps.getUpdateCount();
    }

    @Override
    public <E> List<E> query(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSets(ps);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);
    }
}
