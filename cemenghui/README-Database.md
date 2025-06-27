# 测盟汇管理系统 - 数据库集成指南

## 概述

本指南将帮助你设置数据库和后端服务器，实现用户注册功能的数据持久化。

## 数据库设置

### 1. 数据库结构

根据你提供的SQL文件，系统使用以下数据库结构：

```sql
-- 数据库名：cemenghui
-- 表名：user

CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '用户ID',
  `company` varchar(255) NOT NULL COMMENT '关联企业',
  `username` varchar(255) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `phone` varchar(255) NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NOT NULL COMMENT '性别（0：女；1：男）',
  `status` tinyint NOT NULL COMMENT '状态（0：禁用；1：启用）',
  `is_super` tinyint NOT NULL COMMENT '是否超管（0：否；1：是）',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE,
  INDEX `company_id`(`company` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
```

### 2. 导入数据库

如果你还没有创建数据库，请执行以下步骤：

1. 在MySQL中创建数据库：
```sql
CREATE DATABASE cemenghui CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入SQL文件：
```bash
mysql -u root -p cemenghui < cemenghui.sql
```

## 后端服务器设置

### 1. 创建后端项目目录

```bash
mkdir cemenghui-server
cd cemenghui-server
```

### 2. 复制服务器文件

将 `server-example.js` 和 `server-package.json` 复制到后端项目目录。

### 3. 重命名package.json

```bash
mv server-package.json package.json
```

### 4. 安装依赖

```bash
npm install
```

### 5. 配置数据库连接

编辑 `server-example.js` 文件，修改数据库连接配置：

```javascript
const dbConfig = {
  host: 'localhost',
  user: 'root', // 修改为你的MySQL用户名
  password: '', // 修改为你的MySQL密码
  database: 'cemenghui' // 使用你的实际数据库名
};
```

### 6. 启动服务器

```bash
npm start
```

或者使用开发模式（自动重启）：

```bash
npm run dev
```

服务器将在 `http://localhost:8080` 启动。

## API接口说明

### 注册接口

- **URL**: `POST /register`
- **请求体**:
  ```json
  {
    "username": "用户名",
    "nickname": "昵称",
    "email": "邮箱地址（可选）",
    "phone": "手机号（可选）",
    "password": "密码",
    "companyInfo": "企业信息",
    "gender": 1
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "userId": 1234567890,
      "username": "用户名",
      "nickname": "昵称",
      "company": "企业信息"
    }
  }
  ```

### 登录接口

- **URL**: `POST /login`
- **请求体**:
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "id": 1234567890,
      "username": "用户名",
      "nickname": "昵称",
      "company": "企业信息",
      "phone": "手机号",
      "email": "邮箱地址",
      "gender": 1,
      "isSuper": false
    }
  }
  ```

## 前端配置

前端已经配置为向 `http://localhost:8080` 发送请求。注册表单包含以下字段：

- **必填字段**：用户名、昵称、密码、企业信息
- **可选字段**：邮箱、手机号
- **选择字段**：性别（男/女）

## 安全特性

- 密码使用bcrypt加密存储
- 用户名唯一性验证
- 邮箱唯一性验证（如果提供）
- 输入数据验证和清理
- 错误处理和日志记录
- 用户状态检查（禁用账户无法登录）

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否运行
   - 验证数据库连接配置（用户名、密码、数据库名）
   - 确认数据库和表已创建

2. **CORS错误**
   - 确保后端CORS配置正确
   - 检查前端请求URL

3. **注册失败**
   - 检查控制台错误信息
   - 验证数据库表结构
   - 确认所有必填字段已填写

4. **用户名已存在**
   - 系统会检查用户名唯一性
   - 如果用户名已存在，请使用其他用户名

### 调试模式

启动服务器时添加调试信息：

```bash
DEBUG=* npm start
```

## 数据库字段说明

| 字段名 | 类型 | 说明 | 是否必填 |
|--------|------|------|----------|
| id | bigint | 用户ID（自动生成） | 是 |
| company | varchar(255) | 关联企业 | 是 |
| username | varchar(255) | 账号 | 是 |
| password | varchar(255) | 密码（加密存储） | 是 |
| nickname | varchar(255) | 昵称 | 是 |
| phone | varchar(255) | 手机号 | 否 |
| email | varchar(255) | 邮箱 | 否 |
| gender | tinyint | 性别（0：女；1：男） | 是 |
| status | tinyint | 状态（0：禁用；1：启用） | 是 |
| is_super | tinyint | 是否超管（0：否；1：是） | 是 |
| create_time | datetime | 创建时间 | 是 |

## 下一步

- 添加用户认证中间件
- 实现JWT token认证
- 添加用户权限管理
- 实现密码重置功能
- 添加邮箱验证功能
- 实现用户信息编辑功能 