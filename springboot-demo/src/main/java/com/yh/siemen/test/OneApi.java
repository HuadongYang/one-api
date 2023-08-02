package com.yh.siemen.test;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.executor.ResultWrapper;
import com.yz.oneapi.executor.RoutingExecutor;
import com.yz.oneapi.interceptor.ColumnFill;
import com.yz.oneapi.interceptor.ColumnIdx;
import com.yz.oneapi.interceptor.ColumnTranslate;
import com.yz.oneapi.interceptor.Interceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/one/api")
@RestController
public class OneApi implements InitializingBean {

    @Autowired
    private DataSource dataSource;
    private OneApiConfig oneApiConfig;


    public static ThreadLocal<String> codeThread = new ThreadLocal<>();

    @PostMapping()
    public ResultWrapper one(@RequestBody Map<String, Object> map, HttpServletRequest request) throws SQLException {
        String code = request.getHeader("code");
        codeThread.set(code);
        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();
        ResultWrapper execute = routingExecutor.execute(map);
        return execute;
    }

    public OneApiConfig getOneApiConfig() {
        return oneApiConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        oneApiConfig = new OneApiConfig(dataSource);
        oneApiConfig.setInterceptor(new TestInterceptor());
        oneApiConfig.postProcess();
    }
}
