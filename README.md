# ✨️ 概述
一个API接口实现所有表的增删改查操作。\
\
没有任何第三方依赖，容易集成。\
\
能实现复杂的查询，支持主子表分页。\
\
丰富的扩展点：字典翻译、数据权限、主键策略、逻辑删除等等。\
\
详细文档可见[wiki](https://gitee.com/hhsunshine/one-api/wikis/%E5%89%8D%E8%A8%80)。

-----

# 📕 背景
作者曾深陷CRUD泥潭，每天看似忙忙碌碌，技术和眼界却没有很大提升，究其根本，工作之余，留给自己提升的时间太少。\
\
one-api秉着跳出泥潭的初衷，想通过一个接口尽量做更多的事情，扔掉crud，专注业务和技术。\
\
如果你也有相似的苦恼，可以试试one-api。

----

# ☀️ 亮点
- 一个接口支持所有表的增、删、改、查
- 无任何第三方依赖，包体积很小，对现有系统不会造成任何影响，一分钟内就可完成集成工作
- 支持分页查询，多表关联查询，支持like、in、between、is null、=等条件，支持括号，支持or连接
- 支持批量删除、更新和插入，更新可设置多个条件；批量插入为数据库支持的批量sql，效率高
- 支持的扩展功能很多：别名设置、字典翻译、主键生成、自动填充、逻辑删除、sql打印、数据权限等
- 支持多数据源，一个数据源对应一个接口
- 目前支持mysql和oracle，其他数据库正在适配中……

# ☀️ 支持扩展点
- 别名设置
- 字典翻译
- 主键生成
- 自动填充
- 逻辑删除
- sql打印
- 数据权限
- 数据预热
---

# 使用说明
> 集成web框架，以springboot来举例
- pom引入
    ```
    <dependency>
         <groupId>com.yz</groupId>
         <artifactId>one-api</artifactId>
         <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
- 新建接口 
  ```
  @RequestMapping("/one/api")
  @RestController
  public class OneApi implements InitializingBean {

    @Autowired
    private DataSource dataSource;
    private OneApiConfig oneApiConfig;

    @PostMapping()
    public ResultWrapper one(@RequestBody Map<String, Object> map) throws SQLException {
        RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();
        return routingExecutor.execute(map);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //配置
        oneApiConfig = new OneApiConfig(dataSource);
        //设置扩展点
        oneApiConfig.setInterceptor(new TestInterceptor());
        oneApiConfig.postProcess();
    }
  }

  ```
- 接口调用
> 新增
>> 参数
```
{
    "user": [{
        "name": "leo",
        "age": 5,
        "phoneNumber": 12342345345,
        "isLogin": 1,
        "sex": "m",
        "roleCode": "admin",
        "address": "北京-五道口-星巴克"
    },{
        "name": "jack",
        "age": 21,
        "phoneNumber": 734829,
        "isLogin": 1,
        "sex": "f",
        "roleCode": "normal",
        "address": "山东-章丘-大葱"
    }],
    "role": [
      {
        "code": "admin",
        "label": "管理员"
      },
      {
        "code": "normal",
        "label": "普通角色"
      },
      {
        "code": "manager",
        "label": "经理"
      },
      {
        "code": "example",
        "label": "示例角色"
      }
    ]
}
```
> 新增
>> 返回值
```
{
    "user": 2,
    "role": 4
}
```
> 查询
>> 参数
```
{
   "user":{
     "address@|":"like %五道口%",
     "createTime@":"between 2012-01-01 11:11:11,2042-03-01",
     "roleCode@": "in admin,manager,example",
     "@order":"createTime desc",
     "@page": 1,
     "@size": 3,
     "role":{
         "code@":"/user/roleCode"   
     }
   }
}
```
> 查询
>> 返回值
```
{
    "user": {
        "total": 1,
        "size": 3,
        "page": 1,
        "data": [
            {
                "editor": 124,
                "creator": 123,
                "address": "北京-五道口-星巴克",
                "role": [
                    {
                        "editor": 124,
                        "creator": 123,
                        "code": "admin",
                        "createTime": "2023-08-03T10:14:10.000+0000",
                        "editTime": "2023-08-03T10:14:10.000+0000",
                        "id": 37,
                        "label": "管理员"
                    }
                ],
                "sex": "男",
                "editTime": "2023-08-03T10:15:01.000+0000",
                "isLogin": 1,
                "phoneNumber": "12342345345",
                "createTime": "2023-08-03T10:15:01.000+0000",
                "roleCode": "admin",
                "name": "leo",
                "roleName": "管理员",
                "id": 739546816869040128,
                "age": 5
            }
        ]
    }
}
```
> 更新
>> 参数
```
{
    "user": [{
        "id": 739546816869040128,
        "address": "北京-五道口-枣糕"
    }]
}
```
> 更新
>> 返回值
```
{
    "user": 1
}
```
> 删除
>> 参数
```
{
    "user":[123, 234]
}
```
> 删除
>> 返回值
```
{
    "user": 2
}
```
- 数据展示图，把表信息和扩展信息都在图上展示，可用于开发环境调试\
  ![](https://gitee.com/yanghdhhh/docs/raw/master/oneapi%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%B1%95%E7%A4%BA.jpg)


# 🙏感谢
项目中使用了很多mybaits的源码，感谢🙏，不得不称赞mybatis写的很优雅。


