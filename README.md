# 测盟汇管理系统 | 24-25Training-Practice-cemenghuiWeb

## 一、项目简介/定位

**测盟汇管理系统**是一个基于Vue3 + Spring Boot的综合管理平台，专为高校或组织设计，提供课程管理、会议管理、动态发布、用户管理等核心功能，解决了传统管理方式中信息分散、审核流程复杂、协作效率低下的问题。

---

## 二、主要功能

### 1. 用户管理模块
- 用户注册/登录：支持用户名密码登录，密码加密存储
- 个人信息管理：修改昵称、手机号、邮箱、公司、性别等基本信息
- 密码修改：支持旧密码验证的安全密码修改
- 权限管理：区分普通用户和管理员权限

### 2. 课程管理模块
- 课程发布：支持课程标题、作者、简介、封面图片、视频文件上传
- 课程审核：管理员可审核用户提交的课程内容
- 课程列表：支持搜索、分页、状态筛选
- 课程详情：查看课程完整信息，支持视频预览

### 3. 会议管理模块
- 会议创建：设置会议名称、时间、地点、内容、封面
- 会议审核：管理员审核会议申请
- 会议列表：按时间、状态筛选会议
- 会议编辑：修改会议信息

### 4. 动态管理模块
- 动态发布：发布行业动态、新闻资讯，支持图片上传
- 动态审核：管理员审核动态内容
- 动态列表：查看所有动态，支持搜索
- 动态编辑：修改已发布的动态

---

## 三、安装和运行方法

### 环境要求
- 前端：Node.js 16+，npm 8+
- 后端：JDK 17+，Maven 3.6+
- 数据库：KingBase数据库（已配置）或MySQL

### 前端启动
```bash
cd cemenghui
npm install
npm run dev
```
访问：http://localhost:5173

### 后端启动
```bash
cd Cemenghui_Web
# 配置数据库连接（src/main/resources/application.properties）
mvn spring-boot:run
```
后端接口：http://localhost:8080

### 特殊配置说明
- 数据库使用KingBase，需配置驱动和连接信息
- 文件上传目录：`uploads/`，支持最大100MB文件
- 前端代理配置：`/api` 和 `/findPassword` 代理到后端8080端口

---

## 四、技术栈说明

### 前端技术栈
- 核心框架：Vue 3.5.13 + TypeScript
- 构建工具：Vite 6.0.5
- UI组件库：Element Plus 2.9.2
- 状态管理：Pinia 2.3.0
- 路由管理：Vue Router 4.5.0
- HTTP客户端：Axios 1.7.9
- 图表库：ECharts 5.6.0 + Vue-ECharts 7.0.3
- 开发工具：Vue DevTools

### 后端技术栈
- 核心框架：Spring Boot 3.4.7
- 数据库访问：MyBatis 3.0.4
- 数据库：KingBase 8.6.0（兼容PostgreSQL）
- 构建工具：Maven
- 代码生成：Lombok
- 测试框架：Spring Boot Test + MyBatis Test
- 代码覆盖率：JaCoCo 0.8.7

---

## 五、演示地址、截图或视频

**演示地址**：
- 前端：http://localhost:5173
- 后端API：http://localhost:8080

**主要功能截图**：
- 登录界面：支持用户注册和登录
- 管理员首页：包含用户管理、课程管理、会议管理、动态管理四大模块
- 用户首页：个人信息管理、课程发布、会议创建、动态发布
- 课程管理：支持课程CRUD、审核、视频预览
- 会议管理：会议创建、编辑、审核流程
- 动态管理：新闻发布、审核、列表展示

---

## 六、贡献指南

### 开发规范
- 代码风格：遵循Vue 3 Composition API规范
- 命名规范：使用驼峰命名法，组件名使用PascalCase
- 文件结构：按功能模块组织代码，admin和user分离
- 提交规范：使用语义化提交信息

### 外部贡献
- 欢迎提交Issue和Pull Request
- 新功能开发请先创建Feature分支
- Bug修复请创建Hotfix分支
- 代码提交前请运行测试确保通过

---

## 七、许可证类型

**MIT License** - 允许自由使用、修改和分发，但需保留版权声明。

---

## 八、作者或主要贡献者信息

- 项目维护者：Cemenghui开发团队
- 技术栈：Vue3 + Spring Boot + MyBatis
- 项目地址：[GitHub - 24-25Training-Practice-cemenghuiWeb](https://github.com/Atopos0223/24-25Training-Practice-cemenghuiWeb)

---

## 九、其他补充说明

### 遇到的问题及解决方案
1. 跨域问题：通过Vite代理配置解决前后端跨域
2. 文件上传：配置Spring Boot文件上传大小限制和存储路径
3. 数据库兼容：使用KingBase数据库，兼容PostgreSQL语法
4. 状态管理：使用Pinia替代Vuex，更好的TypeScript支持

### TODO清单
- [ ] 添加JWT token认证机制
- [ ] 实现邮件验证功能
- [ ] 添加数据统计和图表展示
- [ ] 优化移动端适配
- [ ] 添加系统日志记录
- [ ] 实现批量操作功能

### 联系方式
- 技术支持：通过GitHub Issues提交问题
- 功能建议：欢迎提交Feature Request
- Bug报告：请提供详细的错误信息和复现步骤

---

**项目特色**：采用现代化技术栈，界面美观，功能完整，支持完整的CRUD操作和审核流程，适合中小型组织的内部管理系统使用。