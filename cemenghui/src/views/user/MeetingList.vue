<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 会议列表</h2>
    <el-divider />
    <div class="meeting-list">
      <div class="header">
        <el-button type="primary" @click="goToCreate">创建会议</el-button>
      </div>
      
      <div class="filter-container">
        <el-input 
          v-model="searchParams.name" 
          placeholder="会议名称" 
          style="width: 200px"
        />
        <el-date-picker
          v-model="searchParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="filterMeetings">搜索</el-button>
      </div>
      
      <el-table :data="filteredMeetings" border style="width: 100%">
        <el-table-column prop="name" label="会议名称" width="180"/>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{row}">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="会议地点"/>
        <el-table-column prop="status" label="状态">
          <template #default="{row}">
            <el-tag :type="statusTagType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{row}">
            <el-button size="small" @click="viewDetail(row.id)">查看</el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="editMeeting(row)"
              v-if="row.creatorId === currentUserId"
            >编辑</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="confirmDeleteMeeting(row.id)"
              v-if="row.creatorId === currentUserId"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="filterMeetings"
        layout="total, prev, pager, next"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()

const allMeetings = ref([])

const searchParams = reactive({
  name: '',
  dateRange: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const currentUserId = Number(localStorage.getItem('userId'))

const formatDateTime = (dateStr: string) => {
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

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    '已发布': 'success',
    '未审核': '',
    '审核中': 'warning',
    '已通过': 'success',
    '未通过': 'danger'
  }
  return map[status] || ''
}

const statusText = (status: any) => {
  if (status === 1) return '未审核'
  if (status === 2) return '已发布'
  if (status === 3) return '未通过'
  if (status === 0) return '草稿'
  return status
}

// 过滤和分页
const filteredMeetings = computed(() => {
  let result = allMeetings.value.filter(m => {
    const nameMatch = m.name.includes(searchParams.name)
    let dateMatch = true
    if (searchParams.dateRange.length === 2) {
      const start = new Date(searchParams.dateRange[0])
      const end = new Date(searchParams.dateRange[1])
      const meetingDate = new Date(m.startTime)
      dateMatch = meetingDate >= start && meetingDate <= end
    }
    return nameMatch && dateMatch
  })
  pagination.total = result.length
  // 简单分页
  const startIdx = (pagination.current - 1) * pagination.size
  return result.slice(startIdx, startIdx + pagination.size)
})

const filterMeetings = () => {
  pagination.current = 1 // 搜索时回到第一页
}

const viewDetail = (id: number) => {
  router.push(`/userhome/meetingmanage/detail/${id}`)
}

const editMeeting = (row: any) => {
  router.push({ path: '/userhome/meetingmanage/edit', query: { id: row.id } })
}

const confirmDeleteMeeting = (id) => {
  ElMessageBox.confirm(
    '确定要删除该会议吗？此操作不可恢复！',
    '删除确认',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    const res = await request.delete(`/api/meeting/delete/${id}`)
    if (res.data && res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchMeetings()
    } else {
      ElMessage.error(res.data?.message || '删除失败')
    }
  }).catch(() => {})
}

const goToCreate = () => {
  router.push('/userhome/meetingmanage/create')
}

const fetchMeetings = async () => {
  const res = await request.get('/api/meeting/list')
  if (res.data && res.data.code === 200) {
    allMeetings.value = res.data.data.map(item => ({
      ...item,
      name: item.title, // 字段映射，兼容表格 prop="name"
      startTime: item.start_time // 字段映射，兼容表格 prop="startTime"
    }))
  }
}

onMounted(() => {
  fetchMeetings()
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
.el-table {
  border-radius: 12px;
  overflow: hidden;
}
.el-table--striped .el-table__body tr.el-table__row--striped {
  background: #f6faff;
}
.el-table__body tr:hover > td {
  background: #e6f7ff !important;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

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