<template>
  <div class="view-info">
    <el-card>
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
    </el-card>
  </div>
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
.view-info {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>