<template>
  <div class="meeting-detail">
    <el-card v-if="meeting">
      <h3>{{ meeting.name }}</h3>
      <el-descriptions column="2" border>
        <el-descriptions-item label="开始时间">{{ formatDate(meeting.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDate(meeting.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ meeting.creator }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ meeting.status }}</el-descriptions-item>
        <el-descriptions-item label="会议内容" :span="2">{{ meeting.content }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-empty v-else description="未找到会议详情" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const meeting = ref<any>(null)

// 这里用 mock 数据，实际项目应请求后端
const mockMeetings = [
  {
    id: 1,
    name: '2025年第二季度教学研讨会',
    startTime: '2025-06-30T09:00:00',
    endTime: '2025-06-30T11:30:00',
    creator: '王主任',
    content: '讨论第二季度教学成果和第三季度教学计划，包括课程改进、教学方法优化等内容。',
    status: '审核中'
  },
  {
    id: 2,
    name: '职业教育课程开发会议',
    startTime: '2025-07-10T14:00:00',
    endTime: '2025-07-10T16:00:00',
    creator: '李老师',
    content: '讨论职业教育新课程开发方案，制定课程大纲和教学计划。',
    status: '已通过'
  },
  {
    id: 3,
    name: '新课程评审会',
    startTime: '2025-07-15T10:00:00',
    endTime: '2025-07-15T12:00:00',
    creator: '张老师',
    content: '评审新开发的课程内容，确保教学质量和课程标准的符合性。',
    status: '未通过'
  }
]

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

onMounted(() => {
  const id = Number(route.params.id)
  meeting.value = mockMeetings.find(m => m.id === id) || null
})
</script>