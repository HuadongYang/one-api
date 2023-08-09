package com.yz.oneapi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.yz.oneapi.model.ModelFacade;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.orm.executor.SimpleExecutor;
import com.yz.oneapi.orm.mapping.*;
import com.yz.oneapi.orm.session.RowBounds;
import com.yz.oneapi.parser.ast.InsertAst;
import com.yz.oneapi.parser.ast.SelectAst;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

public class DataConfigTest {
    static String field = "SELECT\n" +
            "\tTABLE_NAME AS tableName,\n" +
            "\tCOLUMN_NAME AS name,\n" +
            "\tCOLUMN_COMMENT AS comment,\n" +
            "\tCOLUMN_TYPE AS type,\n" +
            "\tCOLUMN_KEY AS primarys,\n" +
            "\tCHARACTER_MAXIMUM_LENGTH AS dataLength,\n" +
            "\tNUMERIC_PRECISION AS intLength,\n" +
            "\tCOLUMN_DEFAULT AS dataDefaultValue,\n" +
            "\tNUMERIC_SCALE AS dataDot,\n" +
            "\tIS_NULLABLE AS dataIsEmpty,\n" +
            "\tDATA_TYPE AS type2\n" +
            "FROM\n" +
            "\tINFORMATION_SCHEMA.COLUMNS\n" +
            "WHERE\n" +
            "\tTABLE_SCHEMA = 'apijson'\n" +
            "\tAND  TABLE_NAME = 'Access'\n" +
            "ORDER BY\n" +
            "\tTABLE_NAME,\n" +
            "\tORDINAL_POSITION ASC";

    String testSql = "select * from Access";

    @Test
    public void testExecuteQuery() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        SelectAst selectAst = new SelectAst("select * from corner_user");
        ModelFacade modelFacade = oneApiConfig.getModelFacade();

        TableModel tableModel = modelFacade.getModelByModelName("cornerUser");


        List<ResultMapping> resultMappings = new ArrayList<>();
        ResultMapping.Builder rmBuilder = new ResultMapping.Builder(oneApiConfig, "aaakey", "session_key", String.class);
        resultMappings.add(rmBuilder.build());

        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder("corner_user", Map.class, resultMappings, oneApiConfig);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, oneApiConfig, selectAst);

        SimpleExecutor executor = new SimpleExecutor(oneApiConfig);
        List<Object> query = executor.query(sqlStatement, new RowBounds(), null);
        System.out.println(query);
    }
    //从model里获取resultmapping
    @Test
    public void testExecuteQueryObj() throws SQLException, ClassNotFoundException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        SelectAst selectAst = new SelectAst("select * from corner_user");
        ModelFacade modelFacade = oneApiConfig.getModelFacade();

        TableModel tableModel = modelFacade.getModelByModelName("cornerUser");


        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder("corner_user", tableModel.getColumns(), oneApiConfig);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, oneApiConfig, selectAst);

        SimpleExecutor executor = new SimpleExecutor(oneApiConfig);
        List<Object> query = executor.query(sqlStatement, new RowBounds(), null);
        System.out.println(query);
    }

    /**
     * 测试参数映射
     */
    @Test
    public void testExecuteQueryPsParam() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        List<ParameterMapping> parameterMappings = new ArrayList<>();
        ParameterMapping.Builder parameterMapping = new ParameterMapping.Builder(oneApiConfig, "id", Long.class);
        parameterMappings.add(parameterMapping.build());
        ParameterMap parameterMap = new ParameterMap.Builder("Aaa", parameterMappings).build();

        Map<String, Long> params = new HashMap<>();
        params.put("id", 1615147871455158273l);

        SelectAst selectAst = new SelectAst("select * from corner_user where id = ?");
        selectAst.setParameterMap(parameterMap);
        selectAst.setParameter(params);


        List<ResultMapping> resultMappings = new ArrayList<>();
        ResultMapping.Builder rmBuilder = new ResultMapping.Builder(oneApiConfig, "aaakey", "session_key", String.class);
        resultMappings.add(rmBuilder.build());

        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder("corner_user", Map.class, resultMappings, oneApiConfig);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("querylist", resultMaps, oneApiConfig, selectAst);

        SimpleExecutor executor = new SimpleExecutor(oneApiConfig);
        List<Object> query = executor.query(sqlStatement, new RowBounds(), null);
        System.out.println(query);
    }

    @Test
    public void testExecuteUpdate() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        InsertAst insertAst = new InsertAst("INSERT INTO corner.user_feedback\n" +
                "(id, content, create_time, update_time, creator, editor)\n" +
                "VALUES(222, '哈哈 做的不错', 1677764228798, 1677764228805, 1618057515691507714, 1618057515691507714);");
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder builder = new ResultMap.Builder("corner_user_resultmap", Map.class, new ArrayList<>(), oneApiConfig);
        resultMaps.add(builder.build());
        SqlStatement sqlStatement = new SqlStatement("insert", resultMaps, oneApiConfig, insertAst);

        SimpleExecutor executor = new SimpleExecutor(oneApiConfig);
        executor.update(sqlStatement);
    }

    @Test
    public void testExecuteInsert() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());
        User user = new User();
        user.setId(214324l);
        user.setAvatar("34r");
        user.setCid("1234trgh");

        PreparedMapBuilder preparedMapBuilder = new PreparedMapBuilder("user-insrt", oneApiConfig);
        PreparedMapBuilder.PreparedMap preparedMap = preparedMapBuilder.buildInsertMapping("corner_user", user);

        InsertAst insertAst = new InsertAst(preparedMap.getSql());
        insertAst.setParameter(user);
        insertAst.setParameterMap(preparedMap.getParameterMap());


        SqlStatement sqlStatement = new SqlStatement("insert", null, oneApiConfig, insertAst);

        SimpleExecutor executor = new SimpleExecutor(oneApiConfig);
        executor.update(sqlStatement);
    }

    @Test
    public void testClassField() {
        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName() + "-" + field.getType());
        }
    }


    static class User {
        private Long id;
        private String cid;
        private String openId;
        private String sessionKey;
        private String unionid;
        private String status;
        private String token;
        private String avatar;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    @Test
    public void test() {
        long a = 123;
        int b = 123;
        System.out.println((int) a == b);
        System.out.println(System.getProperty("user.dir"));
    }


    public static DataSource dataSource() {
//        Properties prop = new Properties();
//        InputStream in = Object.class.getResourceAsStream("/test.properties");
//        try {
//            prop.load(in);
//            param1 = prop.getProperty("initYears1").trim();
//            param2 = prop.getProperty("initYears2").trim();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Properties properties = new Properties();
        properties.setProperty("druid.url", "jdbc:mysql://localhost:3306/corner?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useTimezone=true&serverTimezone=GMT%2B8&allowMultiQueries=true");
        properties.setProperty("druid.driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("druid.username", "root");
        properties.setProperty("druid.password", "root");
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.configFromPropety(properties);
        druidDataSource.setConnectionInitSqls(Collections.singletonList("set names utf8mb4"));

        try (DruidPooledConnection connection = druidDataSource.getConnection();) {
            druidDataSource.validateConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return druidDataSource;
    }
}