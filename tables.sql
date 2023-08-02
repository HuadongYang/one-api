-------------------------------mysql-------------------------------

-- oneapi.book definition

CREATE TABLE `book` (
  `book_id` varchar(100) NOT NULL COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '书名',
  `writor` varchar(100) DEFAULT NULL COMMENT '作者',
  `wordCount` int(10) DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='书籍';


-- oneapi.comment definition

CREATE TABLE `comment` (
  `comment_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text COMMENT '评论内容',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `book_id` varchar(100) DEFAULT NULL COMMENT '书id',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户对书的评论';


-- oneapi.sys_role definition

CREATE TABLE `sys_role` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) DEFAULT NULL COMMENT '编码',
  `label` varchar(100) DEFAULT NULL COMMENT '名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=234881039 DEFAULT CHARSET=utf8 COMMENT='用户角色表';


-- oneapi.sys_user definition

CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `phone_number` varchar(15) DEFAULT NULL COMMENT '电话号码',
  `is_login` tinyint(1) DEFAULT NULL COMMENT '是否登录',
  `sex` char(1) DEFAULT NULL COMMENT '性别',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';


-- oneapi.sys_user_detail definition

CREATE TABLE `sys_user_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `role_code` varchar(100) DEFAULT NULL COMMENT '角色code',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户详细信息，和用户的关系是多对一';

-------------------------------oracle-------------------------------
CREATE TABLE book1 (
  "book_id" VARCHAR2(100) NOT NULL constraint pk_book_id primary KEY,
  "name" VARCHAR2(100) DEFAULT NULL,
  "writor" VARCHAR2(100) DEFAULT NULL,
  "wordCount" NUMBER(10, 0) DEFAULT NULL
)

create table comment1(
  comment_id NUMBER(10) NOT NULL  constraint pk_co_id primary KEY,
  content VARCHAR2(1000) DEFAULT NULL,
  user_id NUMBER(20) DEFAULT NULL ,
  book_id varchar2(100) DEFAULT NULL ,
  creator NUMBER(20) DEFAULT NULL ,
  create_time date DEFAULT NULL ,
  editor NUMBER(20) DEFAULT NULL ,
  edit_time date DEFAULT NULL
)


create table sys_role(
  id NUMBER(20) NOT NULL constraint pk_role_id primary KEY,
  code varchar2(100) DEFAULT NULL ,
  label varchar2(10) DEFAULT NULL
)

CREATE TABLE sys_user (
  id NUMBER(20) NOT NULL  constraint pk_user_id primary KEY,
 name varchar2(100) DEFAULT NULL,
  age NUMBER(3) DEFAULT NULL,
  phone_number varchar2(15) DEFAULT NULL,
  is_login NUMBER(1) DEFAULT NULL,
  sex varchar2(1) DEFAULT NULL,
  creator NUMBER(20) DEFAULT NULL,
  create_time date DEFAULT NULL,
  editor NUMBER(20) DEFAULT NULL,
  edit_time date DEFAULT NULL
)

CREATE TABLE sys_user_detail (
  id NUMBER(20) NOT NULL constraint pk_detail_id primary KEY,
  user_id NUMBER(20) DEFAULT NULL ,
  address varchar2(100) DEFAULT NULL ,
  role_code varchar2(100) DEFAULT NULL ,
  creator NUMBER(20) DEFAULT NULL ,
  create_time date DEFAULT NULL ,
  editor NUMBER(20) DEFAULT NULL,
  edit_time date DEFAULT NULL

)