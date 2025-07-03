<template>
  <div class="layout-container">
    <!-- 顶部Header -->
    <header class="main-header">
      <h1 class="logo">测盟汇</h1>
      <div class="user-info">
        <span>欢迎，{{ userInfo.username || '超级管理员' }}</span>
        <el-button type="text" @click="logout" class="logout-button">
          退出登录
        </el-button>
      </div>
    </header>

    <div class="content-wrapper">
      <!-- 左侧Aside -->
      <aside class="main-aside">
        <el-menu
          router
          :default-active="$route.path"
          class="system-menu"
          background-color="#fff"
          text-color="#333"
          active-text-color="#409EFF"
          unique-opened
        >
          <!-- 用户管理下拉菜单 -->
          <el-sub-menu index="userManager">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/adminhome/info-management" class="menu-item">
              <span>信息管理</span>
            </el-menu-item>
            <el-menu-item index="/adminhome/permission-management" class="menu-item">
              <span>权限管理</span>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 行业动态下拉菜单 -->
          <el-sub-menu index="industryDynamic">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>行业动态管理</span>
            </template>
            <el-menu-item index="/adminhome/audit-news" class="menu-item">
              <span>审核资讯</span>
            </el-menu-item>
            <el-menu-item index="/adminhome/manage-dynamics" class="menu-item">
              <span>管理动态</span>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 课程管理下拉菜单 -->
          <el-sub-menu index="course-manage">
            <template #title>
              <el-icon><Notebook /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/adminhome/audit-courses" class="menu-item">
              <span>审核课程</span>
            </el-menu-item>
            <el-menu-item index="/adminhome/manage-courses" class="menu-item">
              <span>管理课程</span>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 会议管理下拉菜单 -->
          <el-sub-menu index="meeting-manage">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>会议管理</span>
            </template>
            <el-menu-item index="/adminhome/audit-meetings" class="menu-item">
              <span>审核会议</span>
            </el-menu-item>
            <el-menu-item index="/adminhome/manage-meetings" class="menu-item">
              <span>会议管理</span>
            </el-menu-item>
            <el-menu-item index="/adminhome/create-meeting" class="menu-item">
              <span>创建会议</span>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 个人中心单级菜单 -->
          <el-menu-item index="/adminhome/personal-center" class="menu-item">
            <el-icon><Setting /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 右侧Main -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Setting, TrendCharts, Notebook, Calendar } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const userInfo = ref({})

// 从本地存储获取用户信息
onMounted(() => {
  const userData = localStorage.getItem('userInfo')
  if (userData) {
    try {
      userInfo.value = JSON.parse(userData)
    } catch (error) {
      console.error('解析用户数据失败:', error)
      ElMessage.error('用户信息加载失败，请重新登录')
      logout()
    }
  }
})

const logout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
  ElMessage.success('退出登录成功')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-header {
  height: 60px;
  background: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-button {
  color: white;
}

.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.main-aside {
  width: 220px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
}

.system-menu {
  border-right: none;
  height: 100%;
}

.el-menu-item, .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
  padding-left: 20px;
  font-size: 14px;
  transition: all 0.3s;
}

.el-menu-item:hover, .el-sub-menu.is-active .el-sub-menu__title {
  background: #e6f7ff;
  color: #409EFF;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>