<template>
  <div class="common-layout">
    <!-- 顶部导航栏 -->
    <div class="header bg-white shadow-md">
      <div class="header-content container mx-auto px-4 h-full flex items-center justify-between">
        <!-- 左侧Logo -->
        <div class="header-left flex items-center">
          <span class="logo text-xl font-bold text-gray-800 flex items-center">
            <el-icon class="mr-2 text-blue-500"><Monitor /></el-icon>
            测盟汇管理系统 - 超级管理员
          </span>
        </div>
        
        <!-- 中间搜索栏 -->
        <div class="header-center flex-1 max-w-md mx-4">
          <div class="search-container w-full">
            <el-input 
              v-model="searchKey" 
              placeholder="搜索用户/会议/资讯..." 
              class="search-input w-full"
              clearable
            >
              <template #append>
                <el-button class="search-button bg-blue-500 hover:bg-blue-600 text-white" icon="Search" @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
        
        <!-- 右侧用户信息 -->
        <div class="header-right flex items-center">
          <span class="welcome-text text-gray-700 mr-4">欢迎您，超级管理员：{{ user?.username }}</span>
          <el-button type="text" class="logout-button text-red-500 hover:text-red-600 transition-colors duration-200" @click="logout">
            <el-icon class="mr-1"><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主体内容区：侧边栏 + 主内容 -->
    <div class="main-container flex min-h-screen">
      <!-- 侧边导航栏 -->
      <aside class="custom-aside bg-white border-r border-gray-200 w-64 flex-shrink-0">
        <div class="aside-header p-4 border-b border-gray-200 flex flex-col items-center">
          <div class="avatar mb-3">
            <img :src="user?.profileUrl || 'https://picsum.photos/100/100'" alt="头像" class="w-16 h-16 rounded-full object-cover shadow-md">
          </div>
          <div class="user-name font-medium text-gray-800">{{ user?.username || '系统管理员' }}</div>
          <div class="user-role text-sm text-gray-500">超级管理员</div>
        </div>

        <!-- 核心：垂直导航 + 下拉子菜单 -->
        <el-menu
          class="custom-menu"
          :default-active="activeMenu"
          :unique-opened="true"
          router
          background-color="transparent"
          text-color="#333"
          active-text-color="#409EFF"
          popper-mode="vertical" 
          trigger="hover"       
        >
          <!-- 用户管理 下拉菜单 -->
          <el-sub-menu index="user">
            <template #title>
              <el-icon class="w-6 mr-2"><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/info-management">信息管理</el-menu-item>
            <el-menu-item index="/permission-management">权限管理</el-menu-item>
          </el-sub-menu>

          <!-- 行业动态管理 下拉菜单 -->
          <el-sub-menu index="industry">
            <template #title>
              <el-icon class="w-6 mr-2"><TrendCharts /></el-icon>
              <span>行业动态管理</span>
            </template>
            <el-menu-item index="/audit-news">审核资讯</el-menu-item>
            <el-menu-item index="/manage-dynamics">管理动态</el-menu-item>
          </el-sub-menu>

          <!-- 课程管理 下拉菜单 -->
          <el-sub-menu index="course">
            <template #title>
              <el-icon class="w-6 mr-2"><Notebook /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/audit-courses">审核课程</el-menu-item>
            <el-menu-item index="/manage-courses">管理课程</el-menu-item>
          </el-sub-menu>

          <!-- 会议管理 下拉菜单 -->
          <el-sub-menu index="meeting">
            <template #title>
              <el-icon class="w-6 mr-2"><Calendar /></el-icon>
              <span>会议管理</span>
            </template>
            <el-menu-item index="/audit-meetings">审核会议</el-menu-item>
            <el-menu-item index="/manage-meetings">会议管理</el-menu-item>
            <el-menu-item index="/create-meeting">创建会议</el-menu-item>
          </el-sub-menu>

          <!-- 个人中心 单级菜单 -->
          <el-menu-item index="/personal-center" class="menu-item">
            <el-icon class="w-6 mr-2"><Setting /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="custom-main bg-gray-100 flex-1 p-4">
        <div class="content-padding bg-white rounded-lg shadow-sm p-6">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Monitor,
  Setting,
  User,
  Search,
  SwitchButton,
  TrendCharts,
  Notebook,
  Calendar
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const user = ref<any>(null);
const activeMenu = ref('/info-management'); // 初始激活菜单
const searchKey = ref('');

// 模拟用户数据
const SUPER_ADMIN = {
  username: 'admin',
  role: 'superAdmin',
  profileUrl: 'https://picsum.photos/200/200?random=1'
};



// 监听路由变化，更新激活菜单
watchEffect(() => {
  activeMenu.value = route.path;
});

// 退出登录
const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};

// 搜索（示例）
const handleSearch = () => {
  if (searchKey.value.trim()) {
    ElMessage.info(`搜索关键词: ${searchKey.value}`);
  }
};
</script>

<style scoped>
/* 顶部导航样式 */
.header {
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.header-content {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
}
.header-left, .header-center, .header-right {
  display: flex;
  align-items: center;
}
.header-left { flex: 1; justify-content: flex-start; }
.header-center { flex: 2; max-width: 600px; justify-content: center; }
.header-right { flex: 1; justify-content: flex-end; gap: 15px; }
.logo {
  font-size: 18px;
  font-weight: 600;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 8px;
}
.search-container { width: 100%; }
.search-input :deep(.el-input__inner) {
  height: 36px;
  border-right: none;
  border-radius: 4px 0 0 4px;
}
.search-button {
  border-radius: 0 4px 4px 0;
  height: 36px;
  background-color: #409EFF;
  color: #fff;
  border: none;
}
.search-button:hover { background-color: #66b1ff; }
.welcome-text {
  color: #333;
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
.logout-button:hover { color: #409EFF; }

/* 主体布局 */
.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

/* 侧边栏样式 */
.custom-aside {
  background: #fff;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  padding: 20px 0;
  border-right: 1px solid #f0f2f5;
}
.aside-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
}
.avatar {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 50%;
  border: 3px solid #67c23a;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 8px 0 4px;
}
.user-role {
  font-size: 12px;
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
}

/* 菜单样式 */
.custom-menu {
  border-right: none;
}
.el-menu-item, .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
  margin: 0 12px;
  border-radius: 6px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  font-size: 14px;
}
.el-menu-item:hover, .el-sub-menu.is-active .el-sub-menu__title {
  background: #e6f7ff;
  color: #409EFF;
}
.el-menu-item :deep(.el-icon), 
.el-sub-menu__title :deep(.el-icon) {
  font-size: 20px;
  margin-right: 12px;
  width: 20px;
  height: 20px;
}

/* 子菜单样式（垂直展开） */
:deep(.el-sub-menu .el-menu) {
  width: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 6px 6px;
  background: #fff;
  border: 1px solid #f0f2f5;
  border-top: none;
  margin-top: 4px;
}
:deep(.el-sub-menu .el-menu-item) {
  height: 42px;
  line-height: 42px;
  padding-left: 40px !important;
  border-bottom: 1px solid #f0f2f5;
  border-radius: 0;
}
:deep(.el-sub-menu .el-menu-item:last-child) {
  border-bottom: none;
  border-radius: 0 0 6px 6px;
}

/* 主内容区 */
.custom-main {
  background: #f4f6f9;
  padding: 20px;
}
.content-padding {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 24px;
  min-height: calc(100vh - 100px);
}

/* 响应式适配 */
@media (max-width: 992px) {
  .custom-aside { width: 64px; }
  .aside-header .user-info,
  .el-sub-menu__title span,
  .el-menu-item span { display: none; }
  .el-sub-menu__title, .el-menu-item { justify-content: center; }
  .el-sub-menu__title :deep(.el-icon), 
  .el-menu-item :deep(.el-icon) { margin-right: 0; }
  .header-center { flex: 3; }
}
</style>