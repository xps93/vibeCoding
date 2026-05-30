# 全局技术规范（大厂标准 · 优先级最高）
> 基于阿里《Java开发手册》+ Google Style Guide + GitHub Spec Kit

---

## 一、语言与输出
1. 所有对话、注释、文档：**简体中文**
2. 代码、变量、类名：严格遵循英文命名规范
3. 禁止无意义英文缩写，必要时注释说明

---

## 二、Java 编码规范（强制）
### 1. 命名
- 类：PascalCase（UserService）
- 方法/变量：camelCase（getUserById）
- 常量：UPPER_SNAKE_CASE（MAX_CONNECTIONS）
- 包名：小写（com.xxx.order.service）
- 禁止拼音、中文、下划线混用

### 2. 格式
- 缩进：4 空格
- 单行长度 ≤ 120
- 大括号：K&R 风格（不换行）
- 换行：运算符前换行

### 3. 集合与并发
- Map/Set 键必须重写 equals+hashCode
- 线程安全：ConcurrentHashMap、CopyOnWriteArrayList
- 禁止 foreach 内增删元素

### 4. 异常与日志
- 捕获具体异常，禁止 Exception 吞错
- 日志：时间、级别、方法、参数、异常栈
- 禁止 System.out，使用 SLF4J

---

## 三、MySQL 规范（强制）
- 表/字段：小写+下划线（user_order、create_time）
- 主键：bigint unsigned、自增、非空
- 索引：idx_字段、uniq_字段
- 禁止 SELECT *，必须明确字段
- 禁止存储过程、外键、触发器

---

## 四、工程结构（强制）
- 分层：Controller → Service → Manager → DAO → DO
- 包：com.公司.业务.模块.(controller/service/dao/entity)
- POJO 隔离：DO/DTO/VO/BO 严格区分
- 配置文件：application-{env}.yml 分离

---

## 五、AI 编码安全规则（GitHub 官方）
1. 禁止删除/修改/批量操作，除非用户明确指令
2. 修改文件必须提供：修改前/后 diff + 影响范围
3. 生成代码必须附带：单元测试 + 核心注释
4. 无法确定意图时，必须暂停并确认
5. 禁止编造类、方法、配置，必须基于真实项目

---

## 六、代码质量底线
- 无编译错误
- 无警告（@SuppressWarnings 需说明）
- 关键逻辑注释
- 异常处理完善
- 符合项目原有风格