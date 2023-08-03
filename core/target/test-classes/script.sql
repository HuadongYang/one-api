-- corner.corner_user definition

CREATE TABLE `corner_user` (
  `id` bigint(20) DEFAULT NULL,
  `cid` varchar(100) DEFAULT NULL,
  `open_id` varchar(100) DEFAULT NULL,
  `session_key` varchar(100) DEFAULT NULL,
  `unionid` varchar(100) DEFAULT NULL,
  `status` char(20) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `creator` bigint(20) DEFAULT NULL,
  `editor` bigint(20) DEFAULT NULL,
  `token` varchar(100) DEFAULT NULL,
  `avatar` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;