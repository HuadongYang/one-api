-------------------------------mysql-------------------------------

-- 用户表
CREATE TABLE `oa_user` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `phone_number` varchar(15) DEFAULT NULL COMMENT '电话号码',
  `is_login` tinyint(1) DEFAULT NULL COMMENT '是否登录',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `sex` char(1) DEFAULT NULL COMMENT '性别',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `role_code` varchar(100) DEFAULT NULL COMMENT '角色code',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 角色表
CREATE TABLE `oa_role` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) DEFAULT NULL COMMENT '编码',
  `label` varchar(100) DEFAULT NULL COMMENT '名称',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=234881039 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- 书列表
CREATE TABLE `oa_book` (
  `book_id` varchar(100) NOT NULL COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '书名',
  `writor` varchar(100) DEFAULT NULL COMMENT '作者',
  `wordCount` int(10) DEFAULT NULL,
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor` bigint(20) DEFAULT NULL COMMENT '编辑人',
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='书籍';


-- 书籍评论表
CREATE TABLE `oa_comment` (
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

-- 秘密信息
CREATE TABLE `secret_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text COMMENT '秘密内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秘密信息';


-- 全字段类型测试表
CREATE TABLE oa_fields_types (
  id INT(11) NOT NULL AUTO_increment,
  name VARCHAR(255) NOT NULL,
  age INT(11),
  email VARCHAR(255),
  url VARCHAR(255),
  ip VARCHAR(255),
  date_time DATETIME,
  `date` DATE,
  `time` TIME,
  `year` YEAR,
  enum_value ENUM('value1', 'value2', 'value3'),
  boolean_value BOOLEAN,
  decimal_value DECIMAL(10, 2),
  double_value DOUBLE(10, 2),
  float_value FLOAT(10, 2),
  integer_value INT(11),
  long_value BIGINT(20),
  short_value SMALLINT(6),
  tiny_value TINYINT(4),
  blob_value BLOB,
  json_value JSON,
  default_value VARCHAR(255) DEFAULT 'default value',
  PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字段类型测试';


-------------------------------oracle-------------------------------
-- 书列表
CREATE TABLE oa_book (
    book_id VARCHAR(100) NOT NULL PRIMARY KEY,
    name VARCHAR(100),
    writor VARCHAR(100),
    wordcount NUMBER(10),
    creator NUMBER(20),
    create_time DATE,
    editor NUMBER(20),
    edit_time DATE
)
/

-- 书籍评论表
CREATE TABLE oa_comment (
    comment_id NUMBER(10) NOT NULL PRIMARY KEY,
    content CLOB,
    user_id NUMBER(20),
    book_id VARCHAR(100),
    creator NUMBER(20),
    create_time DATE,
    editor NUMBER(20),
    edit_time DATE
)
/

-- 角色表
CREATE TABLE oa_role (
    id NUMBER(10) NOT NULL PRIMARY key,
    code VARCHAR(100),
    label VARCHAR(100),
    creator NUMBER(20),
    create_time DATE,
    editor NUMBER(20),
    edit_time DATE
)
/
-- 用户表
CREATE TABLE oa_user (
    id NUMBER(20) NOT NULL PRIMARY key,
    name VARCHAR(100),
    age NUMBER(3),
    phone_number VARCHAR(15),
    is_login NUMBER(1),
    is_delete NUMBER(1),
    sex CHAR(1),
    address VARCHAR(100),
    role_code VARCHAR(100),
    creator NUMBER(20),
    create_time DATE,
    editor NUMBER(20),
    edit_time DATE
)
/

-- 全字段类型测试表
CREATE TABLE oa_fields_types (
  id NUMBER,
  name VARCHAR2(100),
  age NUMBER(3),
  email VARCHAR2(255),
  url VARCHAR2(255),
  ip_address VARCHAR2(255),
  date_time DATE,
  "date" DATE,
  enum_value VARCHAR2(255),
  boolean_value NUMBER(1),
  decimal_value NUMBER(10, 2),
  double_value NUMBER(10, 2),
  float_value NUMBER(10, 2),
  integer_value NUMBER(11),
  long_value NUMBER(20),
  short_value NUMBER(6),
  tiny_value NUMBER(4),
  blob_value BLOB,
  json_value CLOB,
  default_value VARCHAR2(255) DEFAULT 'default value',
  PRIMARY KEY (id)
)
/