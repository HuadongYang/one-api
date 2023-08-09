package com.yz.oneapi.config;

import com.yz.oneapi.core.DbType;
import com.yz.oneapi.executor.RoutingExecutor;
import com.yz.oneapi.interceptor.Interceptor;
import com.yz.oneapi.model.ModelFacade;
import com.yz.oneapi.model.meta.MetaRepository;
import com.yz.oneapi.orm.executor.SimpleExecutor;
import com.yz.oneapi.orm.executor.parameter.DefaultParameterHandler;
import com.yz.oneapi.orm.executor.parameter.ParameterHandler;
import com.yz.oneapi.orm.executor.statement.PreparedStatementHandler;
import com.yz.oneapi.orm.executor.statement.StatementHandler;
import com.yz.oneapi.orm.mapping.SqlStatement;
import com.yz.oneapi.orm.reflection.DefaultReflectorFactory;
import com.yz.oneapi.orm.reflection.MetaObject;
import com.yz.oneapi.orm.reflection.ReflectorFactory;
import com.yz.oneapi.orm.reflection.factory.DefaultObjectFactory;
import com.yz.oneapi.orm.reflection.factory.ObjectFactory;
import com.yz.oneapi.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import com.yz.oneapi.orm.reflection.wrapper.ObjectWrapperFactory;
import com.yz.oneapi.orm.resultset.DefaultResultSetHandler;
import com.yz.oneapi.orm.resultset.ResultSetHandler;
import com.yz.oneapi.orm.session.ResultHandler;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.orm.type.JdbcType;
import com.yz.oneapi.orm.type.TypeHandlerRegistry;
import com.yz.oneapi.parser.QueryParser;
import com.yz.oneapi.parser.parser.ParserRegistry;
import com.yz.oneapi.repo.FilePersistence;
import com.yz.oneapi.repo.Persistence;
import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.OneApiUtil;
import com.yz.oneapi.utils.seq.SnowflakeSequence;

import javax.security.auth.login.Configuration;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Supplier;

/**
 * todo：页面：限流
 * todo: 主子表插入、主子表事物
 * todo: 脱敏
 * todo: 系统字段
 * todo: oracle仅支持在Oracle 12c及以上版本中才能使用，能否支持低版本的oracle
 * todo: 4个ast的toString、更新返回主键、插入返回主键
 * todo: 逻辑删除--查询  name LIKE '%l%%'
 * todo: 查询字段
 * todo: 允许缓存扩展实现
 * todo：根据主键的类型自动使用多种不同策略
 * todo：表名、字段大写是否要转为小写
 */
public class OneApiConfig {

    private final DatasourceWrapper datasourceWrapper;

    public static OneApiConfig INSTANCE;
    private MetaRepository metaRepository;
    private ModelFacade modelFacade;
    private Persistence persistence;
    private ParserRegistry parserRegistry;
    private QueryParser queryParser;
    private RoutingExecutor routingExecutor;
    private SimpleExecutor executor;
    private SnowflakeSequence snowflakeSequence;

    /**
     * 缓存的表结构数量
     */
    private int cacheTableCount = 100;
    /**
     * 表结构缓存时间
     */
    private int cacheTableMinutes = 60;

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;
    private Interceptor interceptor;

    private boolean useColumnLabel = true;
    /**
     * 命名风格，默认为下划线
     */
    private Character namingStyle = CharPool.UNDERSCORE;
    /**
     * 转驼峰
     */
    private boolean toCamelCase = true;
    /**
     * 值为空的字段是否返回
     */
    private boolean callSettersOnNulls = false;
    /**
     * 如果选择文件存储，存储文件的路径
     */
    private String persistencePath = System.getProperty("user.dir");
    private JdbcType jdbcTypeForNull = JdbcType.NULL;
    private Integer defaultStatementTimeout;
    private Integer defaultFetchSize;

    private final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
    private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    private ObjectFactory objectFactory = new DefaultObjectFactory();
    private ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();


    public OneApiConfig(DataSource dataSource) {
        datasourceWrapper = new DatasourceWrapper(dataSource);
        INSTANCE = this;
    }

    public OneApiConfig(DataSource dataSource, Configuration configuration) {
        datasourceWrapper = new DatasourceWrapper(dataSource);
        INSTANCE = this;
    }

    public OneApiConfig(DataSource dataSource, String schema, Configuration configuration) {
        datasourceWrapper = new DatasourceWrapper(dataSource, schema);
        INSTANCE = this;
    }


    public DataSource getDatasource() {
        return datasourceWrapper.getDataSource();
    }

    public String getSchema() {
        return datasourceWrapper.getSchema();
    }

    public void setSchema(String schema) {
        datasourceWrapper.setSchema(schema);
    }

    public void setDbType(DbType dbType) {
        datasourceWrapper.setDbType(dbType);
    }

    public DbType getDbType() {
        return datasourceWrapper.getDbType();
    }

    public DatasourceWrapper getDatasourceWrapper() {
        return datasourceWrapper;
    }

    public MetaRepository getMetaRepository() {
        if (metaRepository == null) {
            metaRepository = new MetaRepository(this);
        }
        return metaRepository;
    }

    public ModelFacade getModelFacade() {
        if (modelFacade == null) {
            modelFacade = new ModelFacade(this);
        }
        return modelFacade;
    }

    public QueryParser getQueryParser() {
        if (queryParser == null) {
            queryParser = new QueryParser(this);
        }
        return queryParser;
    }

    public RoutingExecutor getParseExecutor() {
        if (routingExecutor == null) {
            routingExecutor = new RoutingExecutor(this);
        }
        return routingExecutor;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public Persistence getPersistence() {
        if (persistence == null) {
            persistence = new FilePersistence(this);
        }
        return persistence;
    }

    public ParserRegistry newTokenKeyRegistry() {
        if (parserRegistry == null) {
            parserRegistry = ParserRegistry.getInstance();
        }
        return parserRegistry;
    }

    public SimpleExecutor newExecutor() {
        if (executor == null) {
            executor = new SimpleExecutor(this);
        }
        return executor;
    }

    public void setSeq(long workerId, long datacenterId) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public int getCacheTableCount() {
        return cacheTableCount;
    }

    public void setCacheTableCount(int cacheTableCount) {
        this.cacheTableCount = cacheTableCount;
    }

    public int getCacheTableMinutes() {
        return cacheTableMinutes;
    }

    public void setCacheTableMinutes(int cacheTableMinutes) {
        this.cacheTableMinutes = cacheTableMinutes;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }

    public SnowflakeSequence getSnowflakeSequence() {
        if (snowflakeSequence == null) {
            snowflakeSequence = SnowflakeSequence.getSingleton(this.workerId, this.datacenterId);
        }
        return snowflakeSequence;

    }

    public Object getId(String modelName) {
        if (this.interceptor != null && this.interceptor.getId() != null) {
            for (Map.Entry<String, Supplier<Object>> entry : this.interceptor.getId().entrySet()) {
                String k = entry.getKey();
                if (modelName.matches(k)) {
                    return entry.getValue().get();
                }
            }
        }
        return getSnowflakeSequence().nextValue();
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }


    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public boolean isToCamelCase() {
        return toCamelCase;
    }

    public Character getNamingStyle() {
        return namingStyle;
    }

    public void setNamingStyle(Character namingStyle) {
        this.namingStyle = namingStyle;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public void setToCamelCase(boolean toCamelCase) {
        this.toCamelCase = toCamelCase;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public StatementHandler newStatementHandler(SqlStatement sqlStatement, RowBounds rowBounds, ResultHandler resultHandler) {
        return new PreparedStatementHandler(sqlStatement, rowBounds, resultHandler);
    }


    public ResultSetHandler newResultSetHandler(SqlStatement sqlStatement, RowBounds rowBounds, ResultHandler resultHandler) {
        return new DefaultResultSetHandler(sqlStatement, resultHandler, rowBounds);
    }

    public ParameterHandler newParameterHandler(SqlStatement sqlStatement) {
        return new DefaultParameterHandler(sqlStatement);
    }


    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public String getPersistencePath() {
        return persistencePath;
    }

    public void setPersistencePath(String persistencePath) {
        this.persistencePath = persistencePath;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    public void postProcess(){
        warmingData();
    }
    public void warmingData(){
        if (interceptor != null && OneApiUtil.isNotEmpty(interceptor.warmingTable())){
            new Thread(()->{
                interceptor.warmingTable().forEach(modelName->{
                    try {
                        getModelFacade().getModelByModelName(modelName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }
    }
}
