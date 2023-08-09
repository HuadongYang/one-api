# One-API: Dependency-free Database CRUD Tool With One API



One-API is an open-source database manipulation tool designed to simplify the process of working with databases. It allows developers to focus more on business logic and less on the intricacies of database operations. With no third-party dependencies required, you can seamlessly integrate One-API into your web frameworks like Spring Boot. Through a straightforward API interface, you can perform CRUD operations on all tables without the need for entity classes, liberating you from repetitive business logic and giving you more time for creative thinking.

## Features

- **Zero Dependencies**: One-API requires no third-party dependencies, streamlining dependency management for your project.

- **No Entity Classes**: Perform database operations through APIs without the need to write entity classes.

- **Camel Case and Aliases**: Parameters support camel case naming and also allow custom aliases.

- **Complex Queries**: Supports complex query operations to meet various business requirements.

- **Master-Child Table Pagination**: Supports pagination for master and child tables, making handling related data easier.

- **Condition Support**: Offers common query conditions such as `like`, `in`, `between`, `is null`, `=`, with support for parentheses and `or` connections.

- **Batch Operations**: Supports batch delete, update, and insert operations. Update operations can have multiple conditions, while batch inserts utilize efficient batch SQL for improved performance.

- **Extension Features**: Provides rich extension features including alias setting, dictionary translation, primary key generation, auto-filling, logical deletion, SQL printing, data authorization, and more, enhancing flexibility in data operations.

## Getting Started

Here's a simple example of how to use One-API for database operations in a Spring Boot project:

```java
// pom import
<dependency>
  <groupId>com.yz</groupId>
  <artifactId>one-api</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

//new API
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

- API Invocation
> create
>> params
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
> create
>> return data
```
{
    "user": 2,
    "role": 4
}
```
> query
>> params
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
> query
>> return data
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
> update
>> params
```
{
    "user": [{
        "id": 739546816869040128,
        "address": "北京-五道口-枣糕"
    }]
}
```
> update
>> params
```
{
    "user": 1
}
```
> delete
>> params
```
{
    "user":[123, 234]
}
```
> delete
>> return data
```
{
    "user": 2
}
```
- Data Display Diagram: Display table information and extension details on the diagram, which can be utilized for development environment debugging.\
  ![](https://gitee.com/yanghdhhh/docs/raw/master/oneapi%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%B1%95%E7%A4%BA.jpg)



Thank you for choosing One-API to simplify and enhance your database operations. If you have any questions or suggestions, feel free to contact us.


Contact Email: 979078205@qq.com


