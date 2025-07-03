<template>
  <div class="meeting-audit">
    <div class="filter-container">
      <el-select v-model="auditStatus" placeholder="审核状态" clearable>
        <el-option label="全部" value=""/>
        <el-option label="审核中" value="1"/>
        <el-option label="已通过" value="2"/>
        <el-option label="未通过" value="3"/>
        <el-option label="草稿" value="0"/>
      </el-select>
      <el-button type="primary" @click="loadAudits">筛选</el-button>
    </div>
    
    <el-table :data="allAudits" border style="width: 100%">
      <el-table-column prop="id" label="序号" width="80" />
      <el-table-column prop="name" label="会议名称" min-width="120" />
      <el-table-column prop="creator_name" label="提交人" width="100" />
      <el-table-column prop="status" label="审核状态" width="100">
        <template #default="{row}">
          <el-tag :type="statusTagType(row.status)">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="360" align="center">
        <template #default="{row}">
          <div class="button-row">
            <el-button size="small" type="primary" plain @click="viewDetail(row.id)">查看</el-button>
            <el-button size="small" type="success" plain @click="approveAudit(row.id)" v-if="row.status === '1'">通过</el-button>
            <el-button size="small" type="danger" plain @click="rejectAudit(row.id)" v-if="row.status === '1'">拒绝</el-button>
          </div>
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
import request from '@/utils/request'

const rawAudits = ref([])
const allAudits = ref([])
const auditStatus = ref('')
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusTagType = (status: any) => {
  const map: Record<string | number, string> = {
    1: 'warning', // 审核中
    2: 'success', // 已通过
    3: 'danger',  // 未通过
    0: 'info'     // 草稿
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
  let result = rawAudits.value
  if (auditStatus.value !== '' && auditStatus.value !== undefined) {
    result = result.filter(a => String(a.status) === String(auditStatus.value))
  }
  pagination.total = result.length
  // 简单分页
  const startIdx = (pagination.current - 1) * pagination.size
  allAudits.value = result.slice(startIdx, startIdx + pagination.size)
}

const approveAudit = (id: number) => {
  const item = rawAudits.value.find(a => a.id === id)
  if (item) item.status = '2'
  loadAudits()
}

const rejectAudit = (id: number) => {
  const item = rawAudits.value.find(a => a.id === id)
  if (item) item.status = '3'
  loadAudits()
}

const viewDetail = (id: any) => {
  router.push(`/userhome/meetingmanage/detail/${id}`);
};

const statusText = (status: any) => {
  if (status === 1 || status === '1') return '审核中'
  if (status === 2 || status === '2') return '已通过'
  if (status === 3 || status === '3') return '未通过'
  if (status === 0 || status === '0') return '草稿'
  return status
}

const fetchAudits = async () => {
  const res = await request.get('/api/meeting/list')
  if (res.data && res.data.code === 200) {
    rawAudits.value = res.data.data.map(item => ({
      ...item,
      name: item.title
    }))
    loadAudits()
  }
}

onMounted(() => {
  fetchAudits()
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