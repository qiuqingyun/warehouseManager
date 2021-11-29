# 简易的物品库存管理系统

## 编译

执行`gradle build`，在目录`build/libs`中会生成可执行的jar文件。

## 部署

本系统基于`MySQL 8.0`数据库运行，执行项目中的`script.sql`
脚本文件可以创建所需的数据库以及相关表，出于安全考虑，请更改脚本文件中`create user 'warehouseuser'@'%' identified by 'cEZ28iKH8XrLjRL';`语句中的密码。其中`warehouseuser`为MySql数据库账户名称，`cEZ28iKH8XrLjRL`为MySql数据库账户密码。

将生成的jar文件部署到云服务器中，同级目录下创建`config`目录，并在其中创建`application.yml`配置文件，文件内容如下：

```yaml
spring:
  #  数据库连接配置
  datasource:
    url: jdbc:mysql://${MySql数据库地址}:3306/db_warehouse
    username: ${MySql数据库账户名称}
    password: ${MySql数据库账户密码}
http:
  port: 80
server:
  port: 443
  #  Https证书配置
  ssl:
    enabled: true
    key-store-type: ${Https证书类型}
    key-store: ${Https证书位置}
    key-store-password: ${Https证书密码}
```

最后执行`java -jar warehouseManager.jar &`即可运行本系统。

## 账号管理

本系统适用于个人或小范围使用，基于安全考虑，没有提供账户注册功能，添加新账户需连接MySql数据库，并在`db_warehouse`数据库的`user`表中手动添加记录，示例命令如下：

```mysql
INSERT INTO db_warehouse.user (username, password, role_id, account_non_expired, account_non_locked,credentials_non_expired, enabled)
VALUES ('admin', '$2a$10$dzcDm4B4OPPwFUNLDyGl0ulNJ9Nm9UgYsPvmEKwudoRbvU.wuEu6K', 1, 1, 1, 1, 1);
```

示例命令添加了一个账户名称为`admin`，密码为`test`的一个管理员账户，密码在经过10轮`Bcrypt`算法加密后存储在数据库中，即`$2a$10$dzcDm4B4OPPwFUNLDyGl0ulNJ9Nm9UgYsPvmEKwudoRbvU.wuEu6K`，以保证密码的安全性。`Bcrypt`算法可以[在线计算](https://www.jisuan.mobi/p163u3BN66Hm6JWx.html)。