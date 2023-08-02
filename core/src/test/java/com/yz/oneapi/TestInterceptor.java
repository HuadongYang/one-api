package com.yz.oneapi;

import com.yz.oneapi.interceptor.Interceptor;
import com.yz.oneapi.interceptor.SqlCommandType;
import com.yz.oneapi.utils.StringUtil;

public class TestInterceptor implements Interceptor {
    private static long id = 111;





    @Override
    public void printSql(String sql, long executeTimeMills, SqlCommandType sqlCommandType) {
        String sqlLogger = "\n\n==============  Sql Start  ==============\nExecute SQL : {}\nExecute Time: {}\n==============  Sql  End   ==============\n";
        System.out.println(StringUtil.format(sqlLogger, sql, executeTimeMills));
    }
}
