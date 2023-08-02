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
 * todo: 参数处理，复用typeHandler V
 * todo：页面：0、实体映射 1、权限 2、字典翻译--关联表、枚举、方法 3、限流
 * todo：预设requestmapping V
 * todo: 缓存meta，缓存时间可配置；缓存时间内，meta信息不会更新
 * todo: 持久化，默认保存在项目目录，可配置; 数据库实现，利用现成的语法树
 * todo: 参数和返回值使用统一的模型
 * todo: 不同schema的表名不允许重复！！！
 * todo: 不等于 不属于 不在范围内 配置是否要把为null的数据查出来
 * todo: 提供拦截器，允许添加数据权限、角色权限、response包装
 * todo: 主键生成策略、权限扩展、主子表插入、主子表事物
 * todo: 主子表查询
 * todo: 加减字段无需重启
 * todo: 保存模型时，需要清掉所有缓存，比如DefaultResultSetHandler的autoMappingsCache，可基于event事件来做
 * todo：自增主键支持插入返回主键；其他返回自己定义的主键
 * todo: 不同数据库判断主键 mysql：PRI
 * todo: 逻辑删除、脱敏、加数据权限、字段填充、系统字段查询
 * todo: 1、自动填充 2、主子查询 3、主子插入 4、日志打印 5、权限控制 6、函数支持（比如用户id） 7、readme 8、逻辑删除 9、一个表配多个别名
 * todo: 自动填充：怎么方便的对所有表生效
 * todo: 数据预热、适配其他数据库、模型缓存时间
 * todo: 数据权限支持手写sql
 * todo: 什么风格的表名，什么风格的字段名 -—_
 * todo: oracle仅支持在Oracle 12c及以上版本中才能使用
 * todo: 展示页面把拦截器也应该展示上
 * todo: 测试更多字段类型，尽量所有的都测试一下
 * todo: 4个ast的toString、更新返回主键、插入返回主键
 * todo: 压力测试
 */
public class OneApiConfig {

    private DatasourceWrapper datasourceWrapper;

    public static OneApiConfig INSTANCE;
    protected MetaRepository metaRepository;
    protected ModelFacade modelFacade;

    protected Persistence persistence;
    protected ParserRegistry parserRegistry;
    protected QueryParser queryParser;
    private RoutingExecutor routingExecutor;
    protected SimpleExecutor executor;
    protected SnowflakeSequence snowflakeSequence;

    private int cacheTableCount = 100;
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

    protected boolean useColumnLabel = true;
    /**
     * 命名风格，默认为下划线
     */
    protected Character namingStyle = CharPool.UNDERSCORE;
    /**
     * 下划线转驼峰
     */
    protected boolean toCamelCase = true;
    /**
     * 值为空的字段是否返回
     */
    protected boolean callSettersOnNulls = false;
    /**
     * 如果选择文件存储，存储文件的路径
     */
    protected String persistencePath = System.getProperty("user.dir");
    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
    protected Integer defaultStatementTimeout;
    protected Integer defaultFetchSize;

    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();


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
                        getModelFacade().getColumnModelsByModelName(modelName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }
    }
}
