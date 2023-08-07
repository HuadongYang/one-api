package com.yh.siemen.test;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.ResultWrapper;
import com.yz.oneapi.executor.RoutingExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

@RequestMapping("/one/api")
@RestController
public class OneApi implements InitializingBean {
    @Autowired
    private DataSource dataSource;
    private OneApiConfig oneApiConfig;

    @PostMapping()
    public ResultWrapper one(@RequestBody Map<String, Object> map, HttpServletRequest request) throws SQLException {
        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();
        return routingExecutor.execute(map);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        oneApiConfig = new OneApiConfig(dataSource);
        oneApiConfig.setInterceptor(new DemoInterceptor());
        oneApiConfig.postProcess();
    }
    public OneApiConfig getOneApiConfig() {
        return oneApiConfig;
    }
}
