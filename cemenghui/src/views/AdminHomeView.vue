<template>
  <div class="common-layout">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="custom-header">
        <div class="header-content">
          <!-- 左侧Logo -->
          <div class="header-left">
            <span class="logo">
              <el-icon><Monitor /></el-icon>
              测盟汇管理系统 - 超级管理员
            </span>
          </div>
          
          <!-- 中间搜索栏 -->
          <div class="header-center">
            <div class="search-container">
              <el-input 
                v-model="searchKey" 
                placeholder="搜索用户/会议/资讯..." 
                class="search-input"
                clearable
              >
                <template #append>
                  <el-button class="search-button" icon="Search" @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
            </div>
          </div>
          
          <!-- 右侧用户信息和设置 -->
          <div class="header-right">
            <span class="welcome-text">欢迎您，超级管理员：{{user?.username}}</span>
            <el-button type="text" class="logout-button" @click="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-button>
          </div>
        </div>
      </el-header>

      <!-- 主体内容区 -->
      <el-container class="main-container">
        <!-- 侧边导航栏 -->
        <el-aside class="custom-aside" width="220px">
          <div class="aside-header">
            <div class="avatar">
              <img :src="user?.profileUrl || 'https://picsum.photos/100/100'" alt="超级管理员头像">
            </div>
            <div class="user-info">
              <div class="user-name">{{user?.username || '系统管理员'}}</div>
              <div class="user-role">超级管理员</div>
            </div>
          </div>

          <el-menu
            class="custom-menu"
            :default-active="activeMenu"
			:unique-opened="true"
            router
            background-color="transparent"
            text-color="#333"
            active-text-color="#409EFF"
          >
            <!-- 系统管理菜单 -->
            <el-sub-menu index="system" class="menu-item">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/user-management">用户管理</el-menu-item>
              <el-menu-item index="/tenant-management">租户管理</el-menu-item>
              <el-menu-item index="/system-settings">系统设置</el-menu-item>
            </el-sub-menu>
            
            <!-- 内容管理菜单 -->
            <el-sub-menu index="content" class="menu-item">
              <template #title>
                <el-icon><Document /></el-icon>
                <span>内容管理</span>
              </template>
              <el-menu-item index="/meeting-management">会议管理</el-menu-item>
              <el-menu-item index="/industry-dynamic">行业动态</el-menu-item>
              <el-menu-item index="/course-management">课程管理</el-menu-item>
            </el-sub-menu>
            
            <!-- 数据管理菜单 -->
            <el-sub-menu index="data" class="menu-item">
              <template #title>
                <el-icon><PieChart /></el-icon>
                <span>数据管理</span>
              </template>
              <el-menu-item index="/user-analysis">用户数据分析</el-menu-item>
              <el-menu-item index="/meeting-statistics">会议统计</el-menu-item>
              <el-menu-item index="/course-report">课程报告</el-menu-item>
            </el-sub-menu>
            
            <!-- 任务管理菜单 -->
            <el-sub-menu index="task" class="menu-item">
              <template #title>
                <el-icon><List /></el-icon>
                <span>任务管理</span>
              </template>
              <el-menu-item index="/project-tasks">项目任务</el-menu-item>
              <el-menu-item index="/team-management">团队管理</el-menu-item>
              <el-menu-item index="/task-report">任务报告</el-menu-item>
            </el-sub-menu>
            
            <!-- 个人功能 -->
            <el-menu-item index="/personal-center" class="menu-item">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="custom-main">
          <div class="content-padding">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Monitor,
  Setting,
  Document,
  PieChart,
  List,
  User,
  Search,
  SwitchButton
} from '@element-plus/icons-vue';

// 获取路由实例
const router = useRouter();
const route = useRoute();

// 响应式用户数据
const user = ref(null);
// 激活菜单
const activeMenu = ref('/user-management');
// 搜索关键词
const searchKey = ref('');

// 模拟超级管理员用户数据（实际项目中应从本地存储或后端获取）
const SUPER_ADMIN = {
  username: 'admin',
  role: 'superAdmin',
  profileUrl: 'https://picsum.photos/200/200?random=1'
};

// 组件挂载时验证权限
onMounted(() => {
  // 模拟从本地存储获取用户数据
  user.value = localStorage.getItem('user') 
    ? JSON.parse(localStorage.getItem('user')!) 
    : SUPER_ADMIN;
    
  // 初始化激活菜单为当前路由
  activeMenu.value = route.path;
  
  // 权限验证：非超级管理员禁止访问
  if (!user.value || user.value.role !== 'superAdmin') {
    ElMessage.error('无权限访问超级管理员页面');
    router.push('/login');
  }
});

// 监听路由变化更新激活菜单
watchEffect(() => {
  activeMenu.value = route.path;
});

// 退出登录方法
const logout = () => {
  // 清空用户信息
  localStorage.removeItem('user');
  // 跳转到登录页
  router.push('/login');
};

// 搜索功能（实际项目中应调用API）
const handleSearch = () => {
  if (searchKey.value.trim()) {
    ElMessage.info(`搜索关键词: ${searchKey.value}`);
    // router.push(`/search?keyword=${searchKey.value}`);
  }
};
</script>

<style scoped>
/* 继承原有样式并适配测盟汇管理系统需求 */
:root {
  --main-color: #409EFF; /* 主色调：蓝色 */
  --hover-color: #e6f7ff; /* 悬停背景色 */
  --bg-color: #f4f6f9; /* 主体背景色 */
  --text-color: #333; /* 主要文字颜色 */
  --admin-color: #67c23a; /* 管理员标识色：绿色 */
}

.custom-header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.header-left,
.header-center,
.header-right {
  display: flex;
  align-items: center;
}

.header-left {
  flex: 1;
  justify-content: flex-start;
}

.header-center {
  flex: 2;
  max-width: 600px;
  justify-content: center;
}

.header-right {
  flex: 1;
  justify-content: flex-end;
  gap: 15px;
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: var(--admin-color);
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-container {
  display: flex;
  align-items: center;
  width: 100%;
}

.search-input {
  flex: 1;
  border-radius: 4px 0 0 4px;
}

.search-input :deep(.el-input__inner) {
  height: 36px;
  border-right: none;
}

.search-button {
  border-radius: 0 4px 4px 0;
  height: 36px;
  background-color: var(--main-color);
  color: #fff;
  border: none;
}

.search-button:hover {
  background-color: #66b1ff;
}

.welcome-text {
  color: var(--text-color);
  font-size: 14px;
  font-weight: 500;
}

.logout-button {
  font-size: 14px;
  color: #909399;
  transition: color 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.logout-button:hover {
  color: var(--main-color);
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.custom-aside {
  background: #fff;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  padding: 20px 0;
  height: auto !important;
  border-right: 1px solid #f0f2f5;
  position: relative;
  z-index: 900;
}

.aside-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
}

.aside-header .avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 10px;
  overflow: hidden;
  border-radius: 50%;
  border: 3px solid var(--admin-color);
}

.aside-header .avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  text-align: center;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  margin-bottom: 4px;
}

.user-role {
  font-size: 12px;
  color: var(--admin-color);
  background-color: rgba(103, 194, 58, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
}

.custom-menu {
  padding: 0;
  border-right: none;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  margin: 0 12px;
  border-radius: 6px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  font-size: 14px;
  position: relative;
}

.menu-item:hover,
.el-menu-item.is-active {
  background: var(--hover-color);
  color: var(--main-color);
}

.menu-item :deep(.el-icon) {
  font-size: 20px;
  margin-right: 12px;
  width: 20px;
  height: 20px;
}

.el-sub-menu__title {
  display: flex;
  align-items: center;
}

.custom-main {
  background: var(--bg-color);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.content-padding {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 24px;
  min-height: calc(100vh - 100px);
}

/* 关键修复：使子菜单向下展开 */
:deep(.el-sub-menu) {
  position: relative;
}

:deep(.el-sub-menu .el-menu) {
  position: absolute;
  top: 100% !important;
  left: 0 !important;
  width: 100%;
  margin-top: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 6px 6px;
  background: #fff;
  z-index: 100;
}

:deep(.el-sub-menu .el-menu .el-menu-item) {
  height: 42px;
  line-height: 42px;
  padding-left: 20px !important;
}

:deep(.el-sub-menu .el-menu .el-menu-item:first-child) {
  margin-top: 0;
}

:deep(.el-sub-menu .el-menu .el-menu-item:last-child) {
  margin-bottom: 0;
  border-radius: 0 0 6px 6px;
}

/* 修复箭头位置 - 关键修改 */
:deep(.el-sub-menu .el-sub-menu__icon-arrow) {
  position: absolute;
  right: 5px;
  top: 35%;
  transform: translateY(-50%);
  transition: transform 0.3s;
  margin-top: 0;
  font-size: 14px;
}

:deep(.el-sub-menu.is-opened .el-sub-menu__icon-arrow) {
  transform: translateY(-50%) rotate(180deg);
}

:deep(.el-sub-menu.is-opened) {
  background: #f5f7fa;
  border-radius: 6px 6px 0 0;
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  background: #f5f7fa;
  border-radius: 6px 6px 0 0;
  color: var(--main-color);
}

/* 响应式设计 */
@media (max-width: 992px) {
  .custom-aside {
    width: 64px;
  }
  
  .aside-header .user-info,
  .el-sub-menu__title span,
  .el-menu-item span {
    display: none;
  }
  
  .el-sub-menu__title,
  .el-menu-item {
    justify-content: center;
  }
  
  .el-sub-menu__title :deep(.el-icon),
  .el-menu-item :deep(.el-icon) {
    margin-right: 0;
  }
  
  :deep(.el-sub-menu .el-sub-menu__icon-arrow) {
    display: none;
  }
  
  .header-center {
    flex: 3;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
  }
  
  .header-left, 
  .header-center,
  .header-right {
    flex: 1 0 100%;
    justify-content: center;
    margin: 5px 0;
  }
  
  .logo {
    justify-content: center;
  }
  
  .main-container {
    flex-direction: column;
  }
  
  .custom-aside {
    width: 100%;
    height: auto;
    padding: 10px 0;
  }
  
  .aside-header {
    flex-direction: row;
    padding: 10px 20px;
  }
  
  .avatar {
    width: 50px;
    height: 50px;
    margin-bottom: 0;
    margin-right: 15px;
  }
  
  .user-info {
    text-align: left;
  }
  
  .custom-menu {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .el-sub-menu, .el-menu-item {
    margin: 4px;
  }
  
  :deep(.el-sub-menu .el-menu) {
    position: relative;
    width: 100%;
  }
}
</style>