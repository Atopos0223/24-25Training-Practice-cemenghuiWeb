# 用户信息更新功能说明

## 功能概述

现在用户登录后，可以在个人信息管理模块中修改自己的信息，并且修改会同步到数据库中。

## 新增的后端API

### 1. 更新用户信息
- **接口**: `POST /updateUser`
- **参数**: 
  ```json
  {
    "id": 用户ID,
    "nickname": "昵称",
    "phone": "手机号",
    "email": "邮箱",
    "company": "公司",
    "gender": 1  // 1=男, 2=女
  }
  ```
- **返回**: 
  ```json
  {
    "code": 200,
    "message": "success"
  }
  ```

### 2. 修改密码
- **接口**: `POST /updatePassword`
- **参数**:
  ```json
  {
    "username": "用户名",
    "oldPassword": "旧密码",
    "newPassword": "新密码"
  }
  ```
- **返回**:
  ```json
  {
    "code": 200,
    "message": "success"
  }
  ```

## 前端功能

### 1. 基本资料修改 (BasicInfo.vue)
- 显示当前用户的基本信息
- 可以修改昵称、手机号、邮箱、公司、性别
- 保存时调用后端API更新数据库
- 成功后同时更新localStorage中的用户信息

### 2. 修改密码 (ChangePassword.vue)
- 验证旧密码
- 新密码长度验证（最少6位）
- 确认密码一致性验证
- 调用后端API修改数据库中的密码

### 3. 查看信息 (ViewInfo.vue)
- 显示当前用户的完整信息
- 包括用户ID、用户名、昵称、手机号、邮箱、性别、公司、用户类型等
- 信息来源于localStorage，会实时反映最新的修改

## 数据库更新

### 用户信息更新SQL
```sql
UPDATE "user" 
SET "nickname" = ?, "phone" = ?, "email" = ?, "gender" = ?, "company" = ? 
WHERE "id" = ?
```

### 密码更新SQL
```sql
UPDATE "user" 
SET "password" = ? 
WHERE "username" = ?
```

## 使用流程

1. 用户登录后，进入"个人信息管理"模块
2. 在"基本资料修改"页面修改个人信息
3. 点击"保存"按钮，系统会：
   - 调用后端API更新数据库
   - 更新localStorage中的用户信息
   - 显示成功提示
4. 在"查看信息"页面可以看到更新后的信息
5. 在"修改密码"页面可以修改密码，需要验证旧密码

## 注意事项

1. 性别字段在前端显示为中文（男/女），在数据库中存储为数字（1/2）
2. 密码修改需要验证旧密码，确保安全性
3. 所有修改都会实时同步到数据库和localStorage
4. 如果网络错误，会显示相应的错误提示

## 技术实现

### 后端
- 使用MyBatis注解方式编写SQL
- 在Service层进行业务逻辑处理
- 在Controller层提供RESTful API

### 前端
- 使用Vue 3 Composition API
- 使用Axios进行HTTP请求
- 使用Element Plus组件库
- 使用localStorage进行本地数据存储 