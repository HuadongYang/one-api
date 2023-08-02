package com.yz.oneapi.repo;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.orm.executor.SimpleExecutor;
import com.yz.oneapi.orm.mapping.PreparedMapBuilder;
import com.yz.oneapi.orm.mapping.ResultMap;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.parser.ast.InsertAst;
import com.yz.oneapi.parser.ast.SelectAst;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbPersistence extends BasePersistence{
    public DbPersistence(OneApiConfig configuration) {
        super(configuration);
    }

    @Override
    public <T> void write(String tableName, T t) {
        PreparedMapBuilder preparedMapBuilder = new PreparedMapBuilder(tableName + "-insrt", configuration);
        PreparedMapBuilder.PreparedMap preparedMap = preparedMapBuilder.buildInsertMapping(tableName, t);
        InsertAst insertAst = new InsertAst(preparedMap.getSql());
        insertAst.setParameter(t);
        insertAst.setParameterMap(preparedMap.getParameterMap());


        SqlStatement sqlStatement = new SqlStatement("insert", null, configuration, insertAst);

        SimpleExecutor executor = configuration.newExecutor();
        try {
            executor.update(sqlStatement);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> T read(String id, Class<T> clazz) {
        List<T> list = readList(id, clazz);
        if (list != null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T> List<T> readList(String tableName, Class<T> clazz) {
        PreparedMapBuilder preparedMapBuilder = new PreparedMapBuilder(tableName + "-select", configuration);
        PreparedMapBuilder.PreparedMap preparedMap = preparedMapBuilder.buildSelectMapping(tableName);

        SelectAst selectAst = new SelectAst(preparedMap.getSql());
        selectAst.setParameterMap(preparedMap.getParameterMap());
        selectAst.setParameter(null);


        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder(tableName, clazz, null, configuration);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, configuration, selectAst);

        SimpleExecutor executor = configuration.newExecutor();
        try {
            return executor.query(sqlStatement, new RowBounds(), null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
