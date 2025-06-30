<template>
  <div class="meeting-audit">
    <div class="filter-container">
      <el-select v-model="auditStatus" placeholder="审核状态" clearable>
        <el-option label="全部" value=""/>
        <el-option label="审核中" value="审核中"/>
        <el-option label="已通过" value="已通过"/>
        <el-option label="未通过" value="未通过"/>
      </el-select>
      <el-button type="primary" @click="loadAudits">筛选</el-button>
    </div>
    
    <el-table :data="auditList" border style="width: 100%">
      <el-table-column prop="name" label="会议名称" width="180"/>
      <el-table-column prop="creator" label="提交人" width="120"/>
      <el-table-column prop="submitTime" label="提交时间" width="180">
        <template #default="{row}">
          {{ formatDate(row.submitTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="审核状态" width="120">
        <template #default="{row}">
          <el-tag :type="statusTagType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
<el-table-column label="操作" width="200" align="center">
  <template #default="{row}">
    <el-button size="small" @click="viewDetail(row.id)" type="primary" plain>查看</el-button>
    <el-button 
      size="small" 
      type="success" 
      @click="approveAudit(row.id)"
      style="margin-left: 8px"
      v-if="row.status === '审核中'"
    >通过</el-button>
    <el-button 
      size="small" 
      type="danger" 
      @click="rejectAudit(row.id)"
      style="margin-left: 8px"
      v-if="row.status === '审核中'"
    >拒绝</el-button>
  </template>
</el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      @current-change="loadAudits"
      layout="total, prev, pager, next"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import router from '@/router'

const allAudits = ref([
  {
    id: 1,
    name: '2025年第二季度教学研讨会',
    creator: '王主任',
    submitTime: '2025-06-20T10:00:00',
    status: '审核中'
  },
  {
    id: 2,
    name: '职业教育课程开发会议',
    creator: '李老师',
    submitTime: '2025-06-22T14:00:00',
    status: '已通过'
  },
  {
    id: 3,
    name: '新课程评审会',
    creator: '张老师',
    submitTime: '2025-06-25T09:00:00',
    status: '未通过'
  }
])

const auditList = ref<any[]>([])
const auditStatus = ref('')
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    '审核中': 'warning',
    '已通过': 'success',
    '未通过': 'danger'
  }
  return map[status] || ''
}

const formatDate = (dateStr: string) => {
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

const loadAudits = () => {
  let result = allAudits.value
  if (auditStatus.value) {
    result = result.filter(a => a.status === auditStatus.value)
  }
  pagination.total = result.length
  // 简单分页
  const startIdx = (pagination.current - 1) * pagination.size
  auditList.value = result.slice(startIdx, startIdx + pagination.size)
}

const approveAudit = (id: number) => {
  const item = allAudits.value.find(a => a.id === id)
  if (item) item.status = '已通过'
  loadAudits()
}

const rejectAudit = (id: number) => {
  const item = allAudits.value.find(a => a.id === id)
  if (item) item.status = '未通过'
  loadAudits()
}

const viewDetail = (id: any) => {
  router.push(`/userhome/meetingmanage/detail/${id}`);
};

onMounted(() => {
  loadAudits()
})
</script>

<style scoped>
.filter-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>