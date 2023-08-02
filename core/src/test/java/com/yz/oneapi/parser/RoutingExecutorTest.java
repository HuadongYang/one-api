package com.yz.oneapi.parser;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.yz.oneapi.TestInterceptor;
import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.ResultWrapper;
import com.yz.oneapi.executor.RoutingExecutor;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

class RoutingExecutorTest {

    /**
     * 解析测试
     */
    @Test
    public void parseQuery() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());
        oneApiConfig.setInterceptor(new TestInterceptor());

        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> sub = new LinkedHashMap<>();
        sub.put("cid@|(", "like %123%");
        sub.put("openId||", "23");
        sub.put("openId)", "23");
        sub.put("creator", "12223");
        sub.put("@page", 1);
        sub.put("@size", 1);
        map.put("cUser", sub);
        ResultWrapper resultWrapper = routingExecutor.execute(map);
        System.out.println(resultWrapper);
    }

    /**
     * 解析测试
     */
    @Test
    public void parseQuery1() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());
        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> sub = new LinkedHashMap<>();
        sub.put("friends", 3);
        map.put("userSetting", sub);

        Map<String, Object> suub = new LinkedHashMap<>();
        suub.put("openId@", "/userSetting/openid");
        sub.put("cornerUser", suub);

        ResultWrapper resultWrapper = routingExecutor.execute(map);
        System.out.println(resultWrapper);
    }

    @Test
    public void parseInsert() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());
        oneApiConfig.setInterceptor(new TestInterceptor());

        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        HashMap<String, Object> i1 = new HashMap<>();
        i1.put("openId", "leo-auto");
        i1.put("cid", "leo-auto");
        maps.add(i1);
        HashMap<String, Object> i2 = new HashMap<>();
        i2.put("openId", "leo-auto2");
        i2.put("cid", "leo-auto2");
        maps.add(i2);
        map.put("cornerUser", maps);
        ResultWrapper resultWrapper = routingExecutor.execute(map);
    }

    @Test
    public void parseUpdate() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        HashMap<String, Object> i1 = new HashMap<>();
        i1.put("openId", "leo-auto33");
        i1.put("cid", "leo-auto33");
        i1.put("id", 731869601477951488l);
        maps.add(i1);
        HashMap<String, Object> i2 = new HashMap<>();
        i2.put("openId", "leo-auto44");
        i2.put("cid", "leo-auto44");
        i2.put("id", 731869601477951489l);
        maps.add(i2);
        map.put("cornerUser", maps);
        ResultWrapper resultWrapper = routingExecutor.execute(map);
    }

    @Test
    public void parseUpdate1() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        HashMap<String, Object> i1 = new HashMap<>();
        i1.put("openId@", "leo-auto");
        i1.put("cid", "leo-auto55");
        maps.add(i1);
        map.put("cornerUser", maps);
        ResultWrapper resultWrapper = routingExecutor.execute(map);
    }
    @Test
    public void delete() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(dataSource());

        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();


        Map<String, Object> map = new LinkedHashMap<>();
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("112");
        map.put("cornerUser", list);
        ResultWrapper resultWrapper = routingExecutor.execute(map);
    }


    public static DataSource dataSource() {
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