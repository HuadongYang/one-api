package com.yh.siemen.test;

import com.yz.oneapi.interceptor.*;
import com.yz.oneapi.model.ColumnModel;
import com.yz.oneapi.parser.ast.SelectAst;
import com.yz.oneapi.parser.expr.*;
import com.yz.oneapi.utils.Lists;
import com.yz.oneapi.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TestInterceptor implements Interceptor {


    @Override
    public Map<String, Supplier<Object>> getId() {
        Map<String, Supplier<Object>> map = new LinkedHashMap<>();
        //map.put("sysRole", () -> null);
        // comment表的主键自增，设置null，
        map.put("comment", () -> null);
        map.put("book", () -> UUID.randomUUID().toString());
        return map;
    }

    /**
     * 启动时，加载这三张表的元数据
     *
     */
    @Override
    public List<String> warmingTable() {
        return Lists.newArrayList("comment", "book", "sysUser");
    }

    @Override
    public List<TableAlias> alias() {
        List<TableAlias> aliases = new ArrayList<>();
        //sys_user的别名设置为user
        aliases.add(new TableAlias("sys_user", "user"));
        //book表别名设置为book1
        TableAlias book1 = new TableAlias("BOOK1", "book1");
        List<TableAlias.ColumnAlias> columns = new ArrayList<>();
        //book表的book_id字段设置别名为id
        columns.add(new TableAlias.ColumnAlias("book_id", "id"));
        book1.setColumns(columns);
        aliases.add(book1);
        //book表别名设置为book2，这样book1和book2都可以增删改查，可以走不同的数据权限
        aliases.add(new TableAlias("book", "book2"));
        return aliases;
    }

    /**
     * 自动填充
     */
    @Override
    public List<ColumnFill> autoFill() {
        List<ColumnFill> fills = new ArrayList<>();
        fills.add(new ColumnFill(new ColumnIdx(".*", "creator"), SqlCommandType.INSERT, () -> 123));
        fills.add(new ColumnFill(new ColumnIdx(".*", "createTime"), SqlCommandType.INSERT, Date::new));
        fills.add(new ColumnFill(new ColumnIdx(".*", "editor"), SqlCommandType.INSERT, () -> 124));
        fills.add(new ColumnFill(new ColumnIdx(".*", "editTime"), SqlCommandType.INSERT, Date::new));

        fills.add(new ColumnFill(new ColumnIdx(".*", "editor"), SqlCommandType.UPDATE, () -> 125));
        fills.add(new ColumnFill(new ColumnIdx(".*", "editTime"), SqlCommandType.UPDATE, Date::new));
        return fills;
    }

    @Override
    public List<ColumnTranslate> translate() {
        List<ColumnTranslate> list = new ArrayList<>();
        //把roleCode翻译为roleName
        list.add(roleCodeTranslate());
        //把sex翻译为男女
        list.add(sexTranslate());
        //comment表userId翻译为userName
        list.add(userIdTranslate());
        //comment表的bookId翻译为bookName
        list.add(bookIdTranslate());
        return list;
    }

    private static ColumnTranslate sexTranslate() {
        ColumnTranslate translate = new ColumnTranslate(new ColumnIdx(".*", "sex"), "sex");
        translate.setFunction((sex)->sex.equals("f") ? "女":"男");
        return translate;
    }
    private static ColumnTranslate userIdTranslate() {
        ColumnTranslate translate = new ColumnTranslate(new ColumnIdx("comment", "userId"), "userName");
        translate.setBatchFunction(userIds -> {
            JdbcTemplate jdbcTemplate = SpringContextUtil.getBean(JdbcTemplate.class);
            List<Map<String, Object>> labelMap = jdbcTemplate.query(StringUtil.format("select id,name from sys_user where id in ({})", StringUtil.collectionToDelimitedString(userIds, ",", "'", "'")), (resultSet, i) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id",resultSet.getLong("id"));
                map.put("name",resultSet.getString("name"));
                return map;
            });
            Map<Object, Object> id2Name = labelMap.stream().collect(Collectors.toMap(map -> map.get("id"), map -> map.get("name"), (k1, k2) -> k1));
            List<Object> names = new ArrayList<>();
            userIds.forEach(code->names.add(id2Name.get(code)));
            return names;
        });
        return translate;
    }
    private static ColumnTranslate bookIdTranslate() {
        ColumnTranslate translate = new ColumnTranslate(new ColumnIdx("comment", "bookId"), "bookName");
        translate.setBatchFunction(userIds -> {
            JdbcTemplate jdbcTemplate = SpringContextUtil.getBean(JdbcTemplate.class);
            List<Map<String, String>> labelMap = jdbcTemplate.query(StringUtil.format("select book_id,name from book where book_id in ({})", StringUtil.collectionToDelimitedString(userIds, ",", "'", "'")), (resultSet, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("id",resultSet.getString("book_id"));
                map.put("name",resultSet.getString("name"));
                return map;
            });
            Map<String, String> id2Name = labelMap.stream().collect(Collectors.toMap(map -> map.get("id"), map -> map.get("name"), (k1, k2) -> k1));
            List<Object> names = new ArrayList<>();
            userIds.forEach(code->names.add(id2Name.get(code)));
            return names;
        });
        return translate;
    }
    private static ColumnTranslate roleCodeTranslate() {
        ColumnTranslate translate = new ColumnTranslate(new ColumnIdx(".*", "roleCode"), "roleName");
        translate.setBatchFunction(roleCodeSets -> {
            JdbcTemplate jdbcTemplate = SpringContextUtil.getBean(JdbcTemplate.class);
            List<Map<String, String>> labelMap = jdbcTemplate.query(StringUtil.format("select label,code from sys_role where code in ({})", StringUtil.collectionToDelimitedString(roleCodeSets, ",", "'", "'")), (resultSet, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("label",resultSet.getString("label"));
                map.put("code",resultSet.getString("code"));
                return map;
            });
            Map<String, String> code2Label = labelMap.stream().collect(Collectors.toMap(map -> map.get("code"), map -> map.get("label"), (k1, k2) -> k1));
            List<Object> labels = new ArrayList<>();
            roleCodeSets.forEach(code->labels.add(code2Label.get(code)));
            return labels;
        });
        return translate;
    }

    /**
     * 打印sql
     * @param sql              执行的sql
     * @param executeTimeMills 执行的时间
     * @param sqlCommandType   sql类型
     */
    @Override
    public void printSql(String sql, long executeTimeMills, SqlCommandType sqlCommandType) {
        String sqlLogger = "\n\n==============  Sql Start  ==============\nExecute SQL : {}\nExecute Time: {}\n==============  Sql  End   ==============\n";
        System.out.println(StringUtil.format(sqlLogger, sql, executeTimeMills));
    }


    /**
     * 数据权限
     */
    @Override
    public void authority(SelectAst selectAst) {
        //如果不是admin角色且当前查询的是book表，那么书名是"涉密档案"的书不允许返回
        if (!"admin".equals(OneApi.codeThread.get()) && "book".equals(selectAst.getFrom().getAlias())) {
            selectAst.addWhere(new NotEqualExpr(selectAst.getTableModel().getColumnByProperty("name"), "涉密档案"));
        }
        //这个表不能查询
        if ("secretFiles".equals(selectAst.getFrom().getAlias())) {
            selectAst.setAccess(false);
        }
        ColumnModel isDelete = selectAst.getTableModel().getColumnByProperty("isDelete");
        if (isDelete != null && !"book1".equals(selectAst.getTableModel().getModelName())) {
            ParenthesisExpr parenthesisExpr = new ParenthesisExpr();
            selectAst.addWhere(parenthesisExpr)
                    .addWhere(new EqualExpr(isDelete, 0))
                    .addWhere(new OrExpr())
                    .addWhere(new NullExpr(isDelete));
            parenthesisExpr.close();
        }
    }

    @Override
    public List<ColumnLogicDelete> logicDelete() {
        List<ColumnLogicDelete> list = new ArrayList<>();
        list.add(new ColumnLogicDelete(new ColumnIdx(".*", "isDelete"), 1));
        return list;
    }
}
