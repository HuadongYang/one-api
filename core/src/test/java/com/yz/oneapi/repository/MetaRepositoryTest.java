package com.yz.oneapi.repository;

import com.yz.oneapi.config.DataConfigTest;
import com.yz.oneapi.config.OneApiConfig;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MetaRepositoryTest {
    static String field = "SELECT\n" +
            "\tTABLE_NAME AS tableName,\n" +
            "\tCOLUMN_NAME AS column,\n" +
            "\tCOLUMN_COMMENT AS comment,\n" +
            "\tCOLUMN_TYPE AS type,\n" +
            "\tCOLUMN_KEY AS primarys,\n" +
            "\tCHARACTER_MAXIMUM_LENGTH AS dataLength,\n" +
            "\tNUMERIC_PRECISION AS intLength,\n" +
            "\tCOLUMN_DEFAULT AS dataDefaultValue,\n" +
            "\tNUMERIC_SCALE AS dataDot,\n" +
            "\tIS_NULLABLE AS dataIsEmpty,\n" +
            "\tDATA_TYPE AS simpleType\n" +
            "FROM\n" +
            "\tINFORMATION_SCHEMA.COLUMNS\n" +
            "WHERE\n" +
            "\tTABLE_SCHEMA = 'corner' AND\n" +
            "\tTABLE_NAME = 'corner_user'\n" +
            "ORDER BY\n" +
            "\tTABLE_NAME,\n" +
            "\tORDINAL_POSITION ASC";

    @Test
    public void getMetaFields() throws SQLException {
        OneApiConfig oneApiConfig = new OneApiConfig(DataConfigTest.dataSource());
        oneApiConfig.getMetaRepository().getMetaFields("corner", oneApiConfig.getDbType());
    }

}