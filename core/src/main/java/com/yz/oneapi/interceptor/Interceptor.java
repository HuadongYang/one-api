package com.yz.oneapi.interceptor;

import com.yz.oneapi.parser.ast.DeleteAst;
import com.yz.oneapi.parser.ast.InsertAst;
import com.yz.oneapi.parser.ast.SelectAst;
import com.yz.oneapi.parser.ast.UpdateAst;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 扩展点
 */
public interface Interceptor {

    /**
     * 表和字段别名设置
     */
    default List<TableAlias> alias() {
        return null;
    }

    /**
     * 数据预热，返回为表别名的集合
     */
    default List<String> warmingTable() {
        return null;
    }


    /**
     * 字典翻译
     */
    default List<ColumnTranslate> translate() {
        return null;
    }

    /**
     * 主键生成策略，如果为自增主键，请返回一个返回null的Supplier
     * map的key是一个正则表达式，按map的顺序进行匹配
     */
    default Map<String, Supplier<Object>> getId() {
        return null;
    }

    /**
     * 自动填充
     *
     * @return 每个字段的填充规则
     */
    default List<ColumnFill> autoFill() {
        return null;
    }

    /**
     * 打印sql
     *
     * @param sql              执行的sql
     * @param executeTimeMills 执行的时间
     * @param sqlCommandType   sql类型
     */
    default void printSql(String sql, long executeTimeMills, SqlCommandType sqlCommandType) {
    }

    /**
     * 逻辑删除，查询的时候会自动过滤
     */
    default List<ColumnLogicDelete> logicDelete() {
        return null;
    }

    ;

    //权限：查询
    default void authority(SelectAst selectAst) {
    }

    //权限：新增
    default void authority(InsertAst insertAst) {
    }

    //权限：更新
    default void authority(UpdateAst updateAst) {
    }

    //权限：删除
    default void authority(DeleteAst deleteAst) {
    }


}
