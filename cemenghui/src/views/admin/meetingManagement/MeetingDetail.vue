<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 会议详情</h2>
    <el-divider />
    <div class="meeting-detail">
      <el-button @click="$router.back()" style="margin-bottom: 20px;">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      
      <el-card v-if="meeting">
        <h3>{{ meeting.name }}</h3>
        <el-descriptions column="2" border>
          <el-descriptions-item label="开始时间">{{ meeting.startTime || meeting.start_time }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ meeting.endTime || meeting.end_time }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ meeting.creator_name }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusText(meeting.status) }}</el-descriptions-item>
          <el-descriptions-item label="会议内容" :span="2">{{ meeting.content }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-empty v-else description="未找到会议详情" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const meeting = ref<any>(null)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function statusText(status: any) {
  if (status === 1 || status === '1') return '审核中'
  if (status === 2 || status === '2') return '已通过'
  if (status === 3 || status === '3') return '未通过'
  if (status === 0 || status === '0') return '草稿'
  return status
}

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await request.get(`/api/meeting/detail/${id}`)
  if (res.data && res.data.code === 200) {
    const m = res.data.data
    meeting.value = m
  } else {
    meeting.value = null
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
.meeting-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
/* 若有操作按钮，横向排列 */
.button-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
}
.el-table .el-table__cell {
  padding: 12px 16px;
}
</style> 