<template>
  <div class="view-info">
    <el-card>
      <el-descriptions title="个人信息" column="2" border>
        <el-descriptions-item label="昵称">{{ info.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ info.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ info.email }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ info.gender }}</el-descriptions-item>
        <el-descriptions-item label="创建日期">2025-01-01</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const info = reactive({
  nickname: '张三',
  phone: '13800138000',
  email: 'zhangsan@example.com',
  gender: '男'
})
</script>

<style scoped>
.view-info {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>