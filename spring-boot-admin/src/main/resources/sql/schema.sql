-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_test;

-- 用户表
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_menu;

CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) DEFAULT '',
  email VARCHAR(100) DEFAULT '',
  phone VARCHAR(20) DEFAULT '',
  avatar VARCHAR(200) DEFAULT '',
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_key VARCHAR(50) NOT NULL UNIQUE,
  role_name VARCHAR(50) NOT NULL,
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜单表
CREATE TABLE sys_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT DEFAULT 0,
  name VARCHAR(50) NOT NULL,
  path VARCHAR(200) DEFAULT '',
  component VARCHAR(200) DEFAULT '',
  icon VARCHAR(50) DEFAULT '',
  perms VARCHAR(100) DEFAULT '' COMMENT '权限标识',
  menu_type CHAR(1) NOT NULL COMMENT 'M=目录 C=菜单 F=按钮',
  sort INT DEFAULT 0,
  visible INT DEFAULT 0 COMMENT '0=显示 1=隐藏',
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户-角色关联
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色-菜单关联
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id),
  FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
  FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT DEFAULT 0,
  dept_name VARCHAR(50) NOT NULL,
  order_num INT DEFAULT 0,
  leader VARCHAR(50) DEFAULT '',
  phone VARCHAR(20) DEFAULT '',
  email VARCHAR(100) DEFAULT '',
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 岗位表
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_code VARCHAR(50) NOT NULL UNIQUE,
  post_name VARCHAR(50) NOT NULL,
  post_sort INT DEFAULT 0,
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dict_name VARCHAR(100) NOT NULL,
  dict_type VARCHAR(100) NOT NULL UNIQUE,
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dict_type_id BIGINT NOT NULL,
  dict_label VARCHAR(100) NOT NULL,
  dict_value VARCHAR(100) NOT NULL,
  dict_sort INT DEFAULT 0,
  status INT DEFAULT 0 COMMENT '0=正常 1=停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 参数配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_name VARCHAR(100) NOT NULL,
  config_key VARCHAR(100) NOT NULL UNIQUE,
  config_value VARCHAR(500) NOT NULL,
  config_type INT DEFAULT 0 COMMENT '0=内置 1=自定义',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知公告表
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  notice_title VARCHAR(100) NOT NULL,
  notice_type INT DEFAULT 1 COMMENT '1=通知 2=公告',
  notice_content TEXT,
  status INT DEFAULT 0 COMMENT '0=正常 1=关闭',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) DEFAULT '',
  business_type INT DEFAULT 0,
  method VARCHAR(200) DEFAULT '',
  request_method VARCHAR(10) DEFAULT '',
  oper_name VARCHAR(50) DEFAULT '',
  oper_url VARCHAR(200) DEFAULT '',
  oper_ip VARCHAR(50) DEFAULT '',
  oper_param TEXT,
  json_result TEXT,
  status INT DEFAULT 0 COMMENT '0=正常 1=异常',
  error_msg TEXT,
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 登录日志表
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(50) DEFAULT '',
  status INT DEFAULT 0 COMMENT '0=成功 1=失败',
  ip_addr VARCHAR(50) DEFAULT '',
  msg VARCHAR(200) DEFAULT '',
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 定时任务表
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_name VARCHAR(100) NOT NULL,
  job_group VARCHAR(50) DEFAULT '',
  invoke_target VARCHAR(200) DEFAULT '',
  cron_expression VARCHAR(200) DEFAULT '',
  status INT DEFAULT 0 COMMENT '0=正常 1=暂停',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== AI 模块表 ====================

DROP TABLE IF EXISTS ai_message;
DROP TABLE IF EXISTS ai_conversation;
DROP TABLE IF EXISTS ai_model;
DROP TABLE IF EXISTS ai_knowledge_base;
DROP TABLE IF EXISTS ai_config;

-- AI对话表
CREATE TABLE ai_conversation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL DEFAULT '新对话',
  user_id BIGINT NOT NULL,
  model_id VARCHAR(50) DEFAULT '',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_conv_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI消息表
CREATE TABLE ai_message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  conversation_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL COMMENT 'user/assistant/system',
  content TEXT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_msg_conv (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI模型表
CREATE TABLE ai_model (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  provider VARCHAR(50) NOT NULL DEFAULT '',
  capabilities VARCHAR(200) DEFAULT '' COMMENT '逗号分隔: chat,code,reasoning',
  status INT DEFAULT 0 COMMENT '0=启用 1=禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI知识库表
CREATE TABLE ai_knowledge_base (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500) DEFAULT '',
  status INT DEFAULT 0 COMMENT '0=启用 1=禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI用户配置表
CREATE TABLE ai_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  temperature DOUBLE DEFAULT 0.7,
  max_tokens INT DEFAULT 2048,
  system_prompt VARCHAR(2000) DEFAULT '你是一个有帮助的AI助手，请用简洁清晰的中文回答问题。',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== 初始数据 ====================

-- 菜单数据
INSERT INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(1, 0, '首页', '/dashboard', 'dashboard/index', 's-home', '', 'C', 0, 0, 0),
(2, 0, '系统管理', '/system', 'Layout', 'setting', '', 'M', 1, 0, 0),
(3, 2, '用户管理', '/system/user', 'system/user/index', 'user', '', 'C', 0, 0, 0),
(4, 2, '角色管理', '/system/role', 'system/role/index', 's-custom', '', 'C', 1, 0, 0),
(5, 2, '菜单管理', '/system/menu', 'system/menu/index', 'menu', '', 'C', 2, 0, 0),
(6, 3, '用户查询', '', '', '', 'system:user:list', 'F', 0, 0, 0),
(7, 3, '用户新增', '', '', '', 'system:user:create', 'F', 1, 0, 0),
(8, 3, '用户修改', '', '', '', 'system:user:edit', 'F', 2, 0, 0),
(9, 3, '用户删除', '', '', '', 'system:user:delete', 'F', 3, 0, 0),
(10, 4, '角色查询', '', '', '', 'system:role:list', 'F', 0, 0, 0),
(11, 4, '角色新增', '', '', '', 'system:role:create', 'F', 1, 0, 0),
(12, 4, '角色修改', '', '', '', 'system:role:edit', 'F', 2, 0, 0),
(13, 4, '角色删除', '', '', '', 'system:role:delete', 'F', 3, 0, 0),
(14, 5, '菜单查询', '', '', '', 'system:menu:list', 'F', 0, 0, 0),
(15, 5, '菜单新增', '', '', '', 'system:menu:create', 'F', 1, 0, 0),
(16, 5, '菜单修改', '', '', '', 'system:menu:edit', 'F', 2, 0, 0),
(17, 5, '菜单删除', '', '', '', 'system:menu:delete', 'F', 3, 0, 0);

-- 角色数据
INSERT INTO sys_role(id, role_key, role_name, status) VALUES
(1, 'admin', '超级管理员', 0),
(2, 'user', '普通用户', 0);

-- 角色-菜单关联 (admin: 所有菜单, user: 有限菜单)
INSERT INTO sys_role_menu(role_id, menu_id) VALUES
-- admin: all 17 menus
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 9),
(1, 10), (1, 11), (1, 12), (1, 13),
(1, 14), (1, 15), (1, 16), (1, 17),
-- user: dashboard + user list view only
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 9);

-- 用户数据 (密码: {noop}xxx)
INSERT INTO sys_user(id, username, password, nickname, email, phone, status) VALUES
(1, 'admin', '{noop}admin123', '管理员', 'admin@example.com', '13800000000', 0),
(2, 'user', '{noop}user123', '普通用户', 'user@example.com', '13900000000', 0),
(3, 'test', '{noop}test123', '测试员', 'test@example.com', '13700000000', 0);

-- 用户-角色关联
INSERT INTO sys_user_role(user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2);

-- AI模型初始数据
INSERT INTO ai_model(id, name, provider, capabilities, status) VALUES
(1, 'DeepSeek V4 Pro', 'DeepSeek', 'chat,code,reasoning', 0),
(2, 'DeepSeek V4 Flash', 'DeepSeek', 'chat', 0);

-- 数据集表
DROP TABLE IF EXISTS t_dataset_row;
DROP TABLE IF EXISTS t_dataset;
CREATE TABLE t_dataset (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(500) DEFAULT '',
  file_name VARCHAR(200) NOT NULL,
  file_type VARCHAR(20) DEFAULT '' COMMENT 'csv/json/xlsx',
  column_headers TEXT COMMENT '列头JSON数组',
  row_count INT DEFAULT 0,
  user_id BIGINT NOT NULL,
  status INT DEFAULT 0 COMMENT '0=正常 1=删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_dataset_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据集行表
CREATE TABLE t_dataset_row (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dataset_id BIGINT NOT NULL,
  row_index INT DEFAULT 0,
  row_data TEXT COMMENT '行数据JSON',
  INDEX idx_dr_dataset (dataset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI知识库初始数据
INSERT INTO ai_knowledge_base(id, name, description, status) VALUES
(1, '通用知识库', '默认通用知识库', 0);
