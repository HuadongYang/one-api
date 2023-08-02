# ✨️ 概述
one-api是一个没有任何第三方依赖的数据库操作工具。\
把它集成到web框架如springboot里，可以使用一个API接口实现所有表的增删改查操作。\
参数可以使用驼峰风格，也可以自定义别名。\
能实现复杂的查询，也支持主子表分页。\
功能强大，内置了丰富的扩展点。\
它的实现逻辑是DSL -> AST -> SQL，结构简洁，非常容易扩展和魔改。

-----

# 📕 背景
在开发业务系统时，往往新增一张表，至少要写4个接口，增、删、改、查，可能还要处理相似的业务逻辑，比如把male转为男，female转为女，把userId转为userName，拼接查询条件时要判断参数是否存在，如果参数格式不对，还要进行类型转换等等，这些问题减少了我们摸鱼的时间。\
one-api就是想通过一个接口尽量做更多的事情，新增一张表至少普通的crud4个接口不需要写了，而且一次请求可以获取多张表的数据，少了很多工作量。

----

# ☀️ 支持功能
- 1、一个接口支持所有表的增删改查，简单业务接口无需再写
- 2、无任何第三方依赖，包体积很小，对现有系统不会造成任何影响，一分钟内可完成集成工作
- 3、支持分页查询，多表关联查询，查询条件丰富，支持like、in、between、is null、=等条件，支持括号，支持or连接
- 4、支持批量删除、更新和插入，更新可设置多个条件；批量插入为数据库支持的批量sql，效率高
- 4、支持的扩展功能很多：别名设置、字典翻译、主键生成、自动填充、逻辑删除、sql打印、数据权限等
- 5、支持多数据源，一个数据源对应一个接口
- 6、实现的逻辑为DSL->AST->SQL，这样的结构设计更容易魔改
- 7、目前支持mysql和oracle，其他数据库正在适配中……

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
      private AutoApiConfig oneApiConfig;

      @PostMapping()
      public ResultWrapper one(@RequestBody Map<String, Object> map) throws SQLException {
          RoutingExecutor routingExecutor = oneApiConfig.getParseExecutor();
          ResultWrapper execute = routingExecutor.execute(map);
          return execute;
      }

      @Override
      public void afterPropertiesSet() throws Exception {
          //配置
          oneApiConfig = new AutoApiConfig(dataSource);
          //设置扩展点
          oneApiConfig.setInterceptor(new TestInterceptor());
          oneApiConfig.postProcess();
      }

    ```
- 接口调用
> 查询
```
//入参
{
   "User":{
     "(id(":38710,
     "name@|":"like %le%",
     "|createDate@":"between 2012-01-01 11-11-11,2012-03-01",
     "&type@)": "in 1,2,3",
     "@order":["name asc", "createDate desc"],
     "age": 123,
     "@page": 0,
     "@size": 3,
     "userDetails":{
         "userId@":"/User/id"   
     }
   },
   "moment":{
       "userId@": "/user/id"      
   }
}
//返回数据
```
> 新增
```
//入参
{
    "user":[{
        "age": 5,
        "name": "leo"
    },{
        "name": "lucy",
        "age": 3
    }]
}
```
> 更新
```
//入参
{
    "user":[{
        "id": 34,
        "name": 234
    },{
        "name": "leo",
        "type@": "3"
    }]
}
```
> 删除
```
//入参
{
    "user":[123, 234]
}
```

# 🙏感谢
项目中使用了很多mybaits的源码，感谢🙏，不得不称赞mybatis写的很优雅。


