SET NAMES utf8mb4;

-- 菜单数据
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
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
INSERT IGNORE INTO sys_role(id, role_key, role_name, status) VALUES
(1, 'admin', '超级管理员', 0),
(2, 'user', '普通用户', 0);

-- 角色-菜单关联
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 9),
(1, 10), (1, 11), (1, 12), (1, 13),
(1, 14), (1, 15), (1, 16), (1, 17),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 9);

-- 用户数据
INSERT IGNORE INTO sys_user(id, username, password, nickname, email, phone, status) VALUES
(1, 'admin', '$2a$10$uK7PulDtd5YQYePnuDmNiO2AHO1G7yAucFf/NLk2.67TkL4lmpgX2', '管理员', 'admin@example.com', '13800000000', 0),
(2, 'user', '$2a$10$rSJHmGmSYknCjEIdxDj5leSElrP0SY4U98NmGjcmyabSkyK.ekqFi', '普通用户', 'user@example.com', '13900000000', 0),
(3, 'test', '$2a$10$uPLI2hkJ2JN3o2UXou5iBeTOGOXVfFPuTP.vZLzPVO2aTtb8GvXmm', '测试员', 'test@example.com', '13700000000', 0);

-- 用户-角色关联
INSERT IGNORE INTO sys_user_role(user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2);

-- ==================== 新模块菜单数据 ====================

-- 系统管理 → 新子菜单
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(18, 2, '部门管理', '/system/dept', 'system/dept/index', 's-management', '', 'C', 3, 0, 0),
(19, 2, '岗位管理', '/system/post', 'system/post/index', 'postcard', '', 'C', 4, 0, 0),
(20, 2, '字典管理', '/system/dict', 'system/dict/index', 'edit', '', 'C', 5, 0, 0),
(21, 2, '参数管理', '/system/config', 'system/config/index', 'setting', '', 'C', 6, 0, 0),
(22, 2, '通知公告', '/system/notice', 'system/notice/index', 'message', '', 'C', 7, 0, 0);

-- 系统监控目录 + 子菜单
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(23, 0, '系统监控', '/monitor', 'Layout', 'monitor', '', 'M', 2, 0, 0),
(24, 23, '操作日志', '/monitor/operlog', 'monitor/operlog/index', 'document', '', 'C', 0, 0, 0),
(25, 23, '登录日志', '/monitor/loginlog', 'monitor/loginlog/index', 'timer', '', 'C', 1, 0, 0),
(26, 23, '在线用户', '/monitor/online', 'monitor/online/index', 'user', '', 'C', 2, 0, 0),
(27, 23, '定时任务', '/monitor/job', 'monitor/job/index', 'time', '', 'C', 3, 0, 0),
(28, 23, '服务监控', '/monitor/server', 'monitor/server/index', 'monitor', '', 'C', 4, 0, 0),
(29, 23, '缓存监控', '/monitor/cache', 'monitor/cache/index', 'data-analysis', '', 'C', 5, 0, 0),
(30, 23, '连接池监视', '/monitor/druid', 'monitor/druid/index', 'connection', '', 'C', 6, 0, 0);

-- 系统工具目录 + 子菜单
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(31, 0, '系统工具', '/tool', 'Layout', 's-tools', '', 'M', 3, 0, 0),
(32, 31, '代码生成', '/tool/gen', 'tool/gen/index', 's-flag', '', 'C', 0, 0, 0),
(33, 31, '系统接口', '/tool/swagger', 'tool/swagger/index', 'guide', '', 'C', 1, 0, 0),
(34, 31, '在线构建器', '/tool/build', 'tool/build/index', 's-promotion', '', 'C', 2, 0, 0);

-- 新模块按钮权限
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
-- 部门管理按钮
(35, 18, '部门查询', '', '', '', 'system:dept:list', 'F', 0, 0, 0),
(36, 18, '部门新增', '', '', '', 'system:dept:create', 'F', 1, 0, 0),
(37, 18, '部门修改', '', '', '', 'system:dept:edit', 'F', 2, 0, 0),
(38, 18, '部门删除', '', '', '', 'system:dept:delete', 'F', 3, 0, 0),
-- 岗位管理按钮
(39, 19, '岗位查询', '', '', '', 'system:post:list', 'F', 0, 0, 0),
(40, 19, '岗位新增', '', '', '', 'system:post:create', 'F', 1, 0, 0),
(41, 19, '岗位修改', '', '', '', 'system:post:edit', 'F', 2, 0, 0),
(42, 19, '岗位删除', '', '', '', 'system:post:delete', 'F', 3, 0, 0),
-- 字典管理按钮
(43, 20, '字典查询', '', '', '', 'system:dict:list', 'F', 0, 0, 0),
(44, 20, '字典新增', '', '', '', 'system:dict:create', 'F', 1, 0, 0),
(45, 20, '字典修改', '', '', '', 'system:dict:edit', 'F', 2, 0, 0),
(46, 20, '字典删除', '', '', '', 'system:dict:delete', 'F', 3, 0, 0),
-- 参数管理按钮
(47, 21, '参数查询', '', '', '', 'system:config:list', 'F', 0, 0, 0),
(48, 21, '参数新增', '', '', '', 'system:config:create', 'F', 1, 0, 0),
(49, 21, '参数修改', '', '', '', 'system:config:edit', 'F', 2, 0, 0),
(50, 21, '参数删除', '', '', '', 'system:config:delete', 'F', 3, 0, 0),
-- 通知公告按钮
(51, 22, '公告查询', '', '', '', 'system:notice:list', 'F', 0, 0, 0),
(52, 22, '公告新增', '', '', '', 'system:notice:create', 'F', 1, 0, 0),
(53, 22, '公告修改', '', '', '', 'system:notice:edit', 'F', 2, 0, 0),
(54, 22, '公告删除', '', '', '', 'system:notice:delete', 'F', 3, 0, 0),
-- 操作日志按钮
(55, 24, '日志查询', '', '', '', 'monitor:operlog:list', 'F', 0, 0, 0),
(56, 24, '日志删除', '', '', '', 'monitor:operlog:delete', 'F', 1, 0, 0),
-- 登录日志按钮
(57, 25, '日志查询', '', '', '', 'monitor:loginlog:list', 'F', 0, 0, 0),
(58, 25, '日志删除', '', '', '', 'monitor:loginlog:delete', 'F', 1, 0, 0),
-- 在线用户按钮
(59, 26, '在线查询', '', '', '', 'monitor:online:list', 'F', 0, 0, 0),
(60, 26, '在线强退', '', '', '', 'monitor:online:forceLogout', 'F', 1, 0, 0),
-- 定时任务按钮
(61, 27, '任务查询', '', '', '', 'monitor:job:list', 'F', 0, 0, 0),
(62, 27, '任务新增', '', '', '', 'monitor:job:create', 'F', 1, 0, 0),
(63, 27, '任务修改', '', '', '', 'monitor:job:edit', 'F', 2, 0, 0),
(64, 27, '任务删除', '', '', '', 'monitor:job:delete', 'F', 3, 0, 0);

-- 为新模块分配管理员权限 (admin role_id=1 has all new menus)
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES
(1, 18), (1, 19), (1, 20), (1, 21), (1, 22),
(1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30),
(1, 31), (1, 32), (1, 33), (1, 34),
(1, 35), (1, 36), (1, 37), (1, 38),
(1, 39), (1, 40), (1, 41), (1, 42),
(1, 43), (1, 44), (1, 45), (1, 46),
(1, 47), (1, 48), (1, 49), (1, 50),
(1, 51), (1, 52), (1, 53), (1, 54),
(1, 55), (1, 56),
(1, 57), (1, 58),
(1, 59), (1, 60),
(1, 61), (1, 62), (1, 63), (1, 64);

-- 迁移旧的{noop}密码为BCrypt (仅在表中仍存在{noop}前缀时执行)
UPDATE sys_user SET password = '$2a$10$uK7PulDtd5YQYePnuDmNiO2AHO1G7yAucFf/NLk2.67TkL4lmpgX2' WHERE password = '{noop}admin123';
UPDATE sys_user SET password = '$2a$10$rSJHmGmSYknCjEIdxDj5leSElrP0SY4U98NmGjcmyabSkyK.ekqFi' WHERE password = '{noop}user123';
UPDATE sys_user SET password = '$2a$10$uPLI2hkJ2JN3o2UXou5iBeTOGOXVfFPuTP.vZLzPVO2aTtb8GvXmm' WHERE password = '{noop}test123';
