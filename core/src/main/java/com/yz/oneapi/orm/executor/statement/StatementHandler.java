package com.yz.oneapi.orm.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface StatementHandler {
    Statement prepare(Connection connection, Integer transactionTimeout)
            throws SQLException;


    int update(Statement statement)
            throws SQLException;

    <E> List<E> query(Statement statement)
            throws SQLException;

    void parameterize(Statement statement) throws SQLException;


}
