<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 个人信息</h2>
    <el-divider />
    <div class="view-info">
      <el-descriptions title="个人信息" column="2" border>
        <el-descriptions-item label="用户ID">{{ info.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ info.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ info.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ info.phone || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ info.email || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ info.gender }}</el-descriptions-item>
        <el-descriptions-item label="公司">{{ info.company || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ info.is_super === 1 ? '管理员' : '普通用户' }}</el-descriptions-item>
        <el-descriptions-item label="创建日期">{{ info.createTime || '2025-01-01' }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'

const info = reactive({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  email: '',
  gender: '男',
  is_super: 0,
  company: '',
  createTime: ''
})

onMounted(() => {
  // 从localStorage获取用户信息
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    Object.assign(info, userInfo)
  }
})
</script>

<style scoped>
.main-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(64, 158, 255, 0.08);
  padding: 32px 24px;
  background: #fff;
  min-width: 400px;
  margin: 24px 0;
}
.main-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.el-button {
  border-radius: 24px;
  font-size: 16px;
  padding: 8px 32px;
  transition: background 0.2s;
}
.el-button:hover {
  background: #53c0ff;
  color: #fff;
}
.view-info {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>