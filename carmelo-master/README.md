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