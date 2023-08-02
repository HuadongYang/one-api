package com.yz.oneapi.parser;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.model.ModelFacade;
import com.yz.oneapi.parser.ast.SqlAst;

public abstract class AbstractParser implements Parser {
    protected SqlAst ast;
    protected ModelFacade modelFacade;
    protected OneApiConfig oneApiConfig;

    public AbstractParser(OneApiConfig oneApiConfig) {
        this.oneApiConfig = oneApiConfig;
        modelFacade = oneApiConfig.getModelFacade();
    }
}
