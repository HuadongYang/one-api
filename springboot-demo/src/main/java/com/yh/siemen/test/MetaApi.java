package com.yh.siemen.test;

import com.yz.oneapi.config.OneApiConfig;
import com.yz.oneapi.model.ModelFacade;
import com.yz.oneapi.model.TableModel;
import com.yz.oneapi.model.TableModelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meta")
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "dev", matchIfMissing = true)
public class MetaApi  {
    @Autowired
    private DataSource dataSource;
    private OneApiConfig oneApiConfig;
    public MetaApi(OneApi oneApi) {
        //oneApiConfig = new OneApiConfig(dataSource);
        oneApiConfig = oneApi.getOneApiConfig();
    }

    @GetMapping()
    public List<TableModelDTO> getTables() throws SQLException, CloneNotSupportedException {
        ModelFacade modelFacade = oneApiConfig.getModelFacade();
        return modelFacade.getTableMetas();
    }

    @PostMapping("/model/save")
    public void saveModels(@RequestBody List<TableModel> tableModels) throws SQLException {
        ModelFacade modelFacade = oneApiConfig.getModelFacade();
        modelFacade.saveColumnModels(tableModels);
    }


}
