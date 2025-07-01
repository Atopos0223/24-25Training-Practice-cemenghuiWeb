<template>
  <div class="layout-container">
    <!-- 顶部Header -->
    <header class="main-header">
      <h1 class="logo">测盟汇</h1>
      <div class="user-info">
        <span>欢迎，{{ userInfo.username }}</span>
        <el-button type="text" @click="logout"style="color: white;">退出登录</el-button>
      </div>
    </header>

    <div class="content-wrapper">
      <!-- 左侧Aside -->
      <aside class="main-aside">
        <el-menu
          router
          :default-active="$route.path"
          class="system-menu"
        >
              <!-- 修改为下拉菜单 -->
                 <el-sub-menu index="userManager">
                   <template #title>
                     <el-icon><User /></el-icon>
                     <span>个人信息管理</span>
                   </template>
                   <el-menu-item index="/userhome/basic-info" class="menu-item">
                     <span>基本资料修改</span>
                   </el-menu-item>
                   <el-menu-item index="/userhome/change-password" class="menu-item">
                     <span>修改密码</span>
					  </el-menu-item>
					 <el-menu-item index="/userhome/view-info" class="menu-item">
					   <span>查看信息</span>
                   </el-menu-item>
                 </el-sub-menu>
				 
				   <!-- 行业动态下拉菜单 -->
				   <el-sub-menu index="industryDynamic">
					 <template #title>
					   <el-icon><DataBoard /></el-icon>
					   <span>行业动态</span>
					 </template>
					 <el-menu-item index="/userhome/industrydynamic/publish" class="menu-item">
					   <span>发布动态</span>
					 </el-menu-item>
					 <el-menu-item index="/userhome/industrydynamic/list" class="menu-item">
					   <span>动态列表</span>
					 </el-menu-item>
				   </el-sub-menu>
		
				  <!-- 课程管理下拉菜单 -->
				  <el-sub-menu index="course-manage">
					<template #title>
					  <el-icon><Collection /></el-icon>
					  <span>课程管理</span>
					</template>
					<el-menu-item index="/userhome/coursemanage/add" class="menu-item">
					  <span>添加课程</span>
					</el-menu-item>
					<el-menu-item index="/userhome/coursemanage/list" class="menu-item">
					  <span>课程列表</span>
					</el-menu-item>
				  </el-sub-menu>
				  
				  <!-- 会议管理下拉菜单 -->
				  <el-sub-menu index="meeting-manage">
					<template #title>
					  <el-icon><Calendar /></el-icon>
					  <span>会议管理</span>
					</template>
					<el-menu-item index="/userhome/meetingmanage/create" class="menu-item">
					  <span>创建会议</span>
					</el-menu-item>
					<el-menu-item index="/userhome/meetingmanage/list" class="menu-item">
					  <span>会议列表</span>
					</el-menu-item>
					<el-menu-item index="/userhome/meetingmanage/audit" class="menu-item">
					  <span>审核管理</span>
					</el-menu-item>
				  </el-sub-menu>
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
import { useRouter } from 'vue-router'

const router = useRouter()
const userInfo = ref({})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
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