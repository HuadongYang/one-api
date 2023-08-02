package com.yz.oneapi.model;

import com.yz.oneapi.config.OneApiConstant;
import com.yz.oneapi.config.OneApiException;
import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.core.DbType;
import com.yz.oneapi.interceptor.Interceptor;
import com.yz.oneapi.interceptor.TableAlias;
import com.yz.oneapi.model.meta.MetaField;
import com.yz.oneapi.model.meta.MetaRepository;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.NamingCase;
import com.yz.oneapi.utils.cache.Cache;
import com.yz.oneapi.utils.cache.CacheFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelFacade implements OneApiConstant {

    private final MetaRepository metaRepository;
    private final OneApiConfig configuration;
    private final String schema;
    private final DbType dbType;

    //数组0：key为modelName 数组1：key为tableName
    private final Cache<String, Map<String, TableModel>[]> cache;
    private final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();


    public ModelFacade(OneApiConfig configuration) {
        this.metaRepository = configuration.getMetaRepository();
        this.configuration = configuration;
        this.schema = this.configuration.getSchema();
        this.dbType = this.configuration.getDbType();
        this.cache = CacheFactory.newLRUCache(configuration.getCacheTableCount(), configuration.getCacheTableMinutes() * 60000L);
    }

    public List<TableModel> getTableMetas() throws SQLException {
        List<MetaField> metaFields = metaRepository.getMetaFields(schema, dbType);
        List<TableModel> tableModels = ModelFatory.toMultiTableModels(metaFields, configuration);
        if (OneApiUtil.isEmpty(tableModels)) {
            return null;
        }

        Interceptor interceptor = configuration.getInterceptor();

        if (interceptor != null && OneApiUtil.isNotEmpty(interceptor.alias())){
            List<TableModel> addModels = new ArrayList<>();
            List<TableAlias> alias = interceptor.alias();
            Map<String, List<TableAlias>> name2Alias = alias.stream().collect(Collectors.groupingBy(TableAlias::getTableName));

            Iterator<TableModel> it = tableModels.iterator();
            while (it.hasNext()) {
                TableModel next = it.next();
                List<TableAlias> tableAlias = name2Alias.get(next.getTableName());
                if (OneApiUtil.isEmpty(tableAlias)) {
                    continue;
                }
                tableAlias.forEach(item->{
                    try {
                        TableModel clone = next.clone();
                        addModels.add(clone);
                        if (item.getTableAlias() != null) {
                            clone.setModelName(item.getTableAlias());
                        }
                        if (OneApiUtil.isNotEmpty(item.getColumns())){
                            Map<String, TableAlias.ColumnAlias> name2ColAlias = item.getColumns().stream().collect(Collectors.toMap(TableAlias.ColumnAlias::getColumnName, Function.identity()));
                            clone.getColumns().forEach(col->{
                                if (name2ColAlias.containsKey(col.getColumn())) {
                                    TableAlias.ColumnAlias columnAlias = name2ColAlias.get(col.getColumn());
                                    if (OneApiUtil.isNotBlank(columnAlias.getColumnAlias())) {
                                        col.setAlias(columnAlias.getColumnAlias());
                                    }
                                    if (OneApiUtil.isNotBlank(columnAlias.getJavaType())) {
                                        col.setJavaType(columnAlias.getJavaType());
                                    }
                                }
                            });
                        }
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }

                });
                it.remove();
            }
            if (OneApiUtil.isNotEmpty(addModels)) {
                tableModels.addAll(addModels);
            }
        }

        return tableModels;
    }



    private TableModel getColumnModels(String tableName, String modelName, TableAlias tableAlias) throws SQLException {
        TableModel tableModel;
        //模型的所有数据会缓存，如果缓存里没有，直接去元数据查
        if (cache.containsKey(schema) && cache.get(schema)[0].containsKey(modelName)) {
            //缓存中有model
            tableModel = cache.get(schema)[0].get(modelName);
        } else if (cache.containsKey(schema) && cache.get(schema)[1].containsKey(tableName) && tableAlias == null) {
            //缓存中tableName，拦截器中没有对应的model，则说明这个table被手动别名了，自动别名不能被查到
            return null;
        } else {
            //
            Lock lock = lockMap.get(modelName);
            if (lock == null) {
                lock = new Lock();
                Lock oldLock = lockMap.putIfAbsent(modelName, lock);
                if (oldLock != null) {
                    lock = oldLock;
                }
            }
            synchronized (lock) {
                if (cache.containsKey(schema) && cache.get(schema)[0].containsKey(modelName)) {
                    tableModel = cache.get(schema)[0].get(modelName);
                } else {
                    List<MetaField> metaFields = metaRepository.getMetaFields(schema, tableName, dbType);
                    if (OneApiUtil.isEmpty(metaFields)) {
                        throw new OneApiException("not found table for table name : {}", tableName);
                    }
                    List<ColumnModel> columnModels = ModelFatory.toColumnModels(metaFields, configuration);
                    Map<String, TableModel> modelMap = new HashMap<>();
                    tableModel = new TableModel();
                    tableModel.setTableName(tableName);
                    tableModel.setModelName(modelName);
                    tableModel.setColumns(columnModels);
                    modelMap.put(modelName, tableModel);
                    Map<String, TableModel> tableMap = new HashMap<>();
                    tableMap.put(tableName, tableModel);
                    cache.put(schema, new Map[]{modelMap, tableMap});
                }
            }
        }
        //扩展接口的字段别名
        if (tableAlias != null) {
            List<ColumnModel> columns = tableModel.getColumns();
            List<TableAlias.ColumnAlias> columnAlias = tableAlias.getColumns();
            if (OneApiUtil.isNotEmpty(columnAlias)) {
                Map<String, TableAlias.ColumnAlias> column2Alias = columnAlias.stream().collect(Collectors.toMap(TableAlias.ColumnAlias::getColumnName, Function.identity()));
                columns.forEach(x -> {
                    if (column2Alias.containsKey(x.getColumn())) {
                        x.setAlias(column2Alias.get(x.getColumn()).getColumnAlias());
                    }
                });
            }
            tableModel.refresh();
        }

        return tableModel;
    }

    public TableModel getColumnModelsByModelName(String modelName) throws SQLException {
        String tableName = modelName;
        TableAlias tableAlias = null;
        if (configuration.getInterceptor() != null
                && OneApiUtil.isNotEmpty(configuration.getInterceptor().alias())
                && configuration.getInterceptor().alias().stream().anyMatch(x -> modelName.equals(x.getTableAlias()))) {
            tableAlias = configuration.getInterceptor().alias().stream().filter(x -> modelName.equals(x.getTableAlias())).findFirst().orElse(null);
            tableName = tableAlias.getTableName();
        } else {
            if (configuration.isToCamelCase()) {
                tableName = NamingCase.toSymbolCase(modelName, configuration.getNamingStyle());
            }
        }

        TableModel columnModels = getColumnModels(tableName, modelName, tableAlias);
        if (columnModels == null) {
            throw new OneApiException("can not find table info by table alias: {}", modelName);
        }
        columnModels.setModelName(modelName);
        columnModels.getColumns().forEach(x -> x.setModelName(modelName));
        return columnModels;
    }


    public boolean saveColumnModels(List<TableModel> tableModels) {
//        if (AutoApiUtil.isEmpty(tableModels)) {
//            return false;
//        }
//        Persistence persistence = configuration.getPersistence();
//        Map<String, TableModel> tableMap = tableModels.stream().collect(Collectors.toMap(TableModel::getTableName, Function.identity()));
//        persistence.write(AUTO_API_MODEL, tableMap);
//        cache.put(schema, tableMap);
        return true;
    }

    private class Lock {

    }
}
