package com.yz.oneapi.orm.executor;

import com.yz.oneapi.config.OneApiConfig;

import javax.sql.DataSource;

public abstract class BaseExecutor implements Executor {
    protected OneApiConfig configuration;
    protected DataSource dataSource;

    public BaseExecutor(OneApiConfig configuration) {
        this.configuration = configuration;
        this.dataSource = configuration.getDatasource();
    }
}
