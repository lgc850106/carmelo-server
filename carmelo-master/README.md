Carmelo is a fast, scalable Java server framework designed for online games. It uses [Netty](http://netty.io/) and [Fastjson](https://github.com/alibaba/fastjson) for highly efficient network transmission and supports both TCP/HTTP protocols. It also uses [Spring](https://spring.io/) for business logic and [Hibernate](http://hibernate.org/orm/) for data persistence. This framework implements its own servlet to handle client requests, so you can easily extend it to build your own server.



Start from here
----------------------------
**Pre-requisites**: Please have Maven 3.x, Eclipse and MySQL 5.6.x installed. 

Build
-----
1.  git clone https://github.com/needmorecode/carmelo.git
2.  cd carmelo
3.  mvn eclipse:eclipse
4.  Eclipse -> file -> import -> maven -> existing maven projects -> select carmelo project
5.  carmelo project in Eclipse -> right click on pom.xml -> run as -> maven install

Test
-----
1.  cd src/main/java/carmelo/examples 
2.  execute /server/my_test_user.sql in MySQL
3.  run or debug /server/ServerMain.java in Eclipse
4.  run or debug /client/TcpClientMain.java or /client/HttpClientMain.java in Eclipse


by lgc
------------------
这是一个基于netty的服务器-客户端框架，实现了客户端的认证登录机制和自动注册功能，心跳检测功能
采用自定义的通信协议实现Request-Response通信机制。
下一步改进准备优先使用Spring注释配置，并将通信协议拓展为双向对等的Request-Response协议

20181125更新状态，在client端实现了一个随机游走环境，连接服务端后，服务端随机发送游走信号，在客户端可以看到箭头随机改变方向和游走

数据库表格创建：
------------------------------------
create database if not exists my_test;
USE `my_test`;
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `id` int(11) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `compositeId` int(11) not null default 0,
  `lastAcctessTime` datetime not null default 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

select * from User;

rename table user to User;


CREATE TABLE `UserComposite` (
  `id` int(11) NOT NULL,
  `userId` varchar(255) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `children` tinyblob,
  `parentId` int(11),
  `lastAcctessTime` datetime not null default 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `UserBitPort` (
  `id` int(11) NOT NULL,
  `userId` varchar(255) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `readable` bit(1) not null,
  `writeable` bit(1) not null,
  `parentId` int(11),
  `lastAcctessTime` datetime not null default 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;