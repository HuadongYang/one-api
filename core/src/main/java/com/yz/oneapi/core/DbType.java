package com.yz.oneapi.core;

public enum DbType {
    MYSQL("mysql", "MySql数据库"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)");

    /**
     * 数据库名称
     */
    private final String db;
    /**
     * 描述
     */
    private final String desc;

    DbType(String db, String desc) {
        this.db = db;
        this.desc = desc;
    }

    public String getDb() {
        return db;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 获取数据库类型
     *
     * @param dbType 数据库类型字符串
     */
    public static DbType getDbType(String dbType) {
        for (DbType type : DbType.values()) {
            if (type.db.equalsIgnoreCase(dbType)) {
                return type;
            }
        }
        return null;
    }
}
