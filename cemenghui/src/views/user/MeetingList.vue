<template>
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
            {{ row.status }}
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
            v-if="row.status === '未审核'"
          >编辑</el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="deleteMeeting(row.id)"
            v-if="row.status === '未审核'"
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
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// mock 数据
const allMeetings = ref([
  {
    id: 1,
    name: '2025年第二季度教学研讨会',
    startTime: '2025-06-30T09:00:00',
    location: '会议室A',
    status: '已发布'
  },
  {
    id: 2,
    name: '职业教育课程开发会议',
    startTime: '2025-07-10T14:00:00',
    location: '会议室B',
    status: '未审核'
  }
])

const searchParams = reactive({
  name: '',
  dateRange: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

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
  router.push({
    path: '/userhome/meetingmanage/create',
    query: { id: row.id }
  })
}

const deleteMeeting = (id: number) => {
  allMeetings.value = allMeetings.value.filter(m => m.id !== id)
  filterMeetings()
}

const goToCreate = () => {
  router.push('/userhome/meetingmanage/create')
}
</script>

<style scoped>
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