package com.yz.oneapi.orm.executor;

import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(SqlStatement ms, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    int update(SqlStatement sqlStatement) throws SQLException;
}
