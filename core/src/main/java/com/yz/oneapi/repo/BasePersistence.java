package com.yz.oneapi.repo;

import com.yz.oneapi.config.OneApiConfig;

public abstract class BasePersistence implements Persistence {
    protected OneApiConfig configuration;
    public BasePersistence(OneApiConfig configuration) {
        this.configuration = configuration;
    }
}
