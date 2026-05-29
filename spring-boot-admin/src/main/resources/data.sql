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

-- 兼容旧表：为ai_model新增列（兼容MySQL 5.7+，continue-on-error会忽略"列已存在"错误）
ALTER TABLE ai_model ADD COLUMN model_key VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'API模型标识';
ALTER TABLE ai_model ADD COLUMN api_base_url VARCHAR(300) DEFAULT '' COMMENT 'API基础URL';
ALTER TABLE ai_model ADD COLUMN api_key VARCHAR(200) DEFAULT '' COMMENT '模型专属API密钥';
ALTER TABLE ai_model ADD COLUMN sort INT DEFAULT 0 COMMENT '排序号';

-- ==================== AI模型管理菜单与权限 ====================
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(65, 0, 'AI管理', '/ai', 'Layout', 'cpu', '', 'M', 4, 0, 0),
(66, 65, '模型配置', '/ai/model', 'ai/model/index', 'cpu', '', 'C', 0, 0, 0),
(67, 65, '聊天记录', '/ai/chat-record', 'ai/chat-record/index', 'chat-line-round', '', 'C', 1, 0, 0),
(68, 66, '模型查询', '', '', '', 'ai:model:list', 'F', 0, 0, 0),
(69, 66, '模型新增', '', '', '', 'ai:model:create', 'F', 1, 0, 0),
(70, 66, '模型修改', '', '', '', 'ai:model:edit', 'F', 2, 0, 0),
(71, 66, '模型删除', '', '', '', 'ai:model:delete', 'F', 3, 0, 0);

-- 知识库管理菜单与权限
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(72, 65, '知识库管理', '/ai/knowledge', 'ai/knowledge/index', 'edit-outline', '', 'C', 2, 0, 0),
(73, 72, '知识库查询', '', '', '', 'ai:knowledge:list', 'F', 0, 0, 0),
(74, 72, '知识库新增', '', '', '', 'ai:knowledge:create', 'F', 1, 0, 0),
(75, 72, '知识库修改', '', '', '', 'ai:knowledge:edit', 'F', 2, 0, 0),
(76, 72, '知识库删除', '', '', '', 'ai:knowledge:delete', 'F', 3, 0, 0);

-- 为管理员分配AI管理权限
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES
(1, 65), (1, 66), (1, 67),
(1, 68), (1, 69), (1, 70), (1, 71),
(1, 72), (1, 73), (1, 74), (1, 75), (1, 76);

-- AI模型初始数据
INSERT IGNORE INTO ai_model(id, model_key, name, provider, capabilities, api_base_url, api_key, sort, status) VALUES
(1, 'deepseek-chat', 'DeepSeek Chat', 'deepseek', 'chat,code,reasoning', '', '', 0, 0),
(2, 'deepseek-reasoner', 'DeepSeek Reasoner', 'deepseek', 'chat,reasoning', '', '', 1, 0),
(3, 'gpt-3.5-turbo', 'GPT-3.5 Turbo', 'openai', 'chat,code', '', '', 2, 1),
(4, 'gpt-4o', 'GPT-4o', 'openai', 'chat,code,reasoning', '', '', 3, 1),
(5, 'qwen-turbo', '通义千问 Turbo', 'qwen', 'chat,code', '', '', 4, 1),
(6, 'qwen-plus', '通义千问 Plus', 'qwen', 'chat,code,reasoning', '', '', 5, 1),
(7, 'glm-4', 'ChatGLM-4', 'zhipu', 'chat,code,reasoning', '', '', 6, 1),
(8, 'moonshot-v1-8k', 'Moonshot v1 8K', 'moonshot', 'chat,code', '', '', 7, 1);

-- 兼容旧数据：更新已存在模型的model_key（仅当model_key为空时）
UPDATE ai_model SET model_key = 'deepseek-chat' WHERE id = 1 AND (model_key IS NULL OR model_key = '');
UPDATE ai_model SET model_key = 'deepseek-reasoner' WHERE id = 2 AND (model_key IS NULL OR model_key = '');
-- 禁用未配置密钥的模型提供商（DeepSeek已配置密钥保持启用）
UPDATE ai_model SET status = 1 WHERE provider IN ('openai', 'qwen', 'zhipu', 'moonshot') AND status = 0;

-- AI知识库初始数据
INSERT IGNORE INTO ai_knowledge_base(id, name, description, status) VALUES
(1, '通用知识库', '默认通用知识库', 0);

-- 迁移旧的{noop}密码为BCrypt (仅在表中仍存在{noop}前缀时执行)
UPDATE sys_user SET password = '$2a$10$uK7PulDtd5YQYePnuDmNiO2AHO1G7yAucFf/NLk2.67TkL4lmpgX2' WHERE password = '{noop}admin123';
UPDATE sys_user SET password = '$2a$10$rSJHmGmSYknCjEIdxDj5leSElrP0SY4U98NmGjcmyabSkyK.ekqFi' WHERE password = '{noop}user123';
UPDATE sys_user SET password = '$2a$10$uPLI2hkJ2JN3o2UXou5iBeTOGOXVfFPuTP.vZLzPVO2aTtb8GvXmm' WHERE password = '{noop}test123';

-- ==================== 站点配置初始数据 ====================
INSERT IGNORE INTO sys_site_config(id, config_key, config_value, config_name, remark) VALUES
(1, 'site_name', 'Ds-Ai', '站点名称', '页面标题和logo旁显示'),
(2, 'site_logo', '', '站点Logo', 'Logo图片URL'),
(3, 'site_copyright', '© 2026 Ds-Ai. All Rights Reserved.', '版权信息', '页脚版权声明'),
(4, 'site_description', '基于AI大模型的智能对话平台', '站点描述', 'SEO description'),
(5, 'site_keywords', 'AI,聊天,大模型,DeepSeek,GPT', '站点关键词', 'SEO keywords');

-- ==================== 助手分类与模板初始数据 ====================
INSERT IGNORE INTO ai_assistant_category(id, name, sort, status) VALUES
(1, '通用助手', 0, 0),
(2, '编程开发', 1, 0),
(3, '写作翻译', 2, 0),
(4, '数据分析', 3, 0);

INSERT IGNORE INTO ai_assistant(id, category_id, name, description, icon, prompt, sort, status) VALUES
(1, 1, '通用AI助手', '通用AI对话助手，回答各类问题', 'ChatDotRound', '你是一个有帮助的AI助手，请用简洁清晰的中文回答问题。', 0, 0),
(2, 1, '创意灵感', '帮你发散思维，激发创意灵感', 'Sunny', '你是一个创意灵感助手。请为用户的想法提供多样化的视角和有创意的建议，鼓励发散性思维。', 1, 0),
(3, 2, '代码专家', '编程问题解答、代码生成与调试', 'Monitor', '你是一个资深软件工程师。请提供清晰的代码示例和详细的解释，遵循最佳实践，注意安全性和性能优化。使用markdown代码块格式输出代码。', 0, 0),
(4, 2, 'SQL专家', 'SQL编写、优化与数据库设计', 'DataAnalysis', '你是一个数据库专家。请为用户编写高效、安全的SQL语句，解释查询计划，并提供数据库设计和优化建议。', 1, 0),
(5, 2, 'Bug修复', '代码Debug与错误分析', 'Warning', '你是一个专业的Debug工程师。请帮助用户分析代码错误原因，提供修复方案，并解释根本原因。', 2, 0),
(6, 3, '中英翻译', '专业中英文双向翻译', 'Connection', '你是一个专业翻译。请将用户输入的内容翻译成对应语言，保持原意和语境，使用自然流畅的表达。如用户输入中文则翻译为英文，输入英文则翻译为中文。', 0, 0),
(7, 3, '文案写作', '广告文案、营销内容创作', 'Edit', '你是一个资深文案策划。请根据用户需求创作有吸引力的文案，注重说服力和转化率，语言精炼有力。', 1, 0),
(8, 3, '学术润色', '学术论文语言润色与修改', 'Document', '你是一个学术写作专家。请帮助用户润色学术文本，保持学术风格和严谨表达，指出语法和逻辑问题。', 2, 0),
(9, 4, '数据解读', '数据分析报告解读与洞察', 'TrendCharts', '你是一个数据分析专家。请帮助用户理解数据背后的含义，提供数据驱动的洞察和建议。', 0, 0);

-- ==================== 助手管理菜单与权限 ====================
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(77, 65, '助手分类', '/ai/assistant-category', 'ai/assistant-category/index', 's-grid', '', 'C', 3, 0, 0),
(78, 65, '助手模板', '/ai/assistant', 'ai/assistant/index', 'chat-dot-square', '', 'C', 4, 0, 0),
(79, 77, '分类查询', '', '', '', 'ai:assistant:category:list', 'F', 0, 0, 0),
(80, 77, '分类新增', '', '', '', 'ai:assistant:category:create', 'F', 1, 0, 0),
(81, 77, '分类修改', '', '', '', 'ai:assistant:category:edit', 'F', 2, 0, 0),
(82, 77, '分类删除', '', '', '', 'ai:assistant:category:delete', 'F', 3, 0, 0),
(83, 78, '模板查询', '', '', '', 'ai:assistant:list', 'F', 0, 0, 0),
(84, 78, '模板新增', '', '', '', 'ai:assistant:create', 'F', 1, 0, 0),
(85, 78, '模板修改', '', '', '', 'ai:assistant:edit', 'F', 2, 0, 0),
(86, 78, '模板删除', '', '', '', 'ai:assistant:delete', 'F', 3, 0, 0);

-- 站点配置管理菜单与权限
INSERT IGNORE INTO sys_menu(id, parent_id, name, path, component, icon, perms, menu_type, sort, visible, status) VALUES
(87, 2, '站点配置', '/system/site-config', 'system/site-config/index', 'setting', '', 'C', 8, 0, 0),
(88, 87, '配置查询', '', '', '', 'system:site-config:list', 'F', 0, 0, 0),
(89, 87, '配置修改', '', '', '', 'system:site-config:edit', 'F', 1, 0, 0);

-- 为管理员分配新菜单权限
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES
(1, 77), (1, 78), (1, 79), (1, 80), (1, 81), (1, 82),
(1, 83), (1, 84), (1, 85), (1, 86),
(1, 87), (1, 88), (1, 89);

-- ==================== 系统错误码初始数据（中英文双语） ====================
-- 错误码范围规划：
--   1-999    通用系统错误
--   400-499  HTTP层错误（参数、认证、权限等）
--   500-599  服务端错误（数据库、第三方等）
--   1000-1999 用户与业务模块错误
--   2000-2999 AI模块错误
INSERT IGNORE INTO sys_error_code(code, zh_msg, en_msg, module) VALUES
(1, '服务器繁忙，请稍后重试', 'Service is busy, please try again later', 'system'),
(400, '请求参数不合法', 'Invalid request parameters', 'system'),
(401, '未登录或登录已过期，请重新登录', 'Not logged in or session expired, please login again', 'system'),
(403, '权限不足，无法访问该资源', 'Access denied, insufficient permissions', 'system'),
(404, '请求的资源不存在', 'The requested resource was not found', 'system'),
(405, '请求方式不支持', 'Request method not supported', 'system'),
(429, '请求过于频繁，请稍后再试', 'Too many requests, please try again later', 'system'),
(500, '系统内部错误，请联系管理员', 'Internal server error, please contact administrator', 'system'),
(501, '数据库操作异常，请联系管理员', 'Database operation error, please contact administrator', 'system'),
(502, '第三方服务调用异常，请稍后重试', 'Third-party service error, please try again later', 'system'),
(1001, '用户名或密码错误', 'Incorrect username or password', 'user'),
(1002, '用户名已存在', 'Username already exists', 'user'),
(1003, '用户不存在', 'User does not exist', 'user'),
(1004, '用户已被停用', 'User has been disabled', 'user'),
(1005, '验证码错误或已过期', 'Verification code is invalid or expired', 'user'),
(1006, '手机号格式不正确', 'Invalid phone number format', 'user'),
(1007, '旧密码不正确', 'Incorrect old password', 'user'),
(1008, '新密码不能与旧密码相同', 'New password must differ from old password', 'user'),
(1101, '角色不存在', 'Role does not exist', 'role'),
(1102, '角色标识已存在', 'Role key already exists', 'role'),
(1201, '菜单不存在', 'Menu does not exist', 'menu'),
(1202, '菜单存在子菜单，无法删除', 'Cannot delete menu with children', 'menu'),
(2001, 'AI模型不存在', 'AI model does not exist', 'ai'),
(2002, 'AI模型已被禁用', 'AI model is disabled', 'ai'),
(2003, '对话不存在或无权访问', 'Conversation not found or access denied', 'ai'),
(2004, 'AI服务调用失败，请检查模型配置', 'AI service call failed, please check model configuration', 'ai'),
(2005, 'AI服务响应超时', 'AI service response timeout', 'ai'),
(2006, '当前对话正在生成中，请稍后', 'Conversation is currently generating, please wait', 'ai'),
(2007, '知识库不存在', 'Knowledge base does not exist', 'ai'),
(2008, '助手模板不存在', 'Assistant template does not exist', 'ai'),
(2009, '站点配置键不存在', 'Site config key does not exist', 'ai');
