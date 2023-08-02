package com.yz.oneapi.config;

import com.yz.oneapi.core.DbType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatasourceWrapper {
    private DataSource dataSource;
    private String schema;
    private DbType dbType;

    public DatasourceWrapper(DataSource dataSource) {
        this.dataSource = dataSource;
        try(Connection connection = this.dataSource.getConnection();) {
            this.dbType = DbType.getDbType(connection.getMetaData().getDatabaseProductName());
            if (this.dbType == DbType.MYSQL) {
                this.schema = connection.getCatalog();
            }else {
                this.schema = connection.getSchema();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public DatasourceWrapper(DataSource dataSource, String schema) {
        this.dataSource = dataSource;
        this.schema = schema;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }
}
