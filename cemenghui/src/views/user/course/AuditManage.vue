<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 审核管理</h2>
    <el-divider />
    <div class="audit-manage">
      <el-button @click="router.back()">返回</el-button>
      
      <el-table :data="audits" border>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="status" label="审核状态">
          <template #default="{row}">
            <el-tag :type="getStatusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="viewDetail(row)">查看</el-button>
            <el-button type="success" size="small" @click="approve(row)" 
              v-if="row.status === '审核中'">通过</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const audits = ref([
  {
    id: 1,
    name: 'Python数据分析',
    author: '李老师',
    status: '审核中',
    intro: '本课程介绍Python在数据分析中的应用，包括pandas、numpy等库的使用。',
    coverUrl: '/src/assets/logo.svg',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4'
  }
])

const getStatusTagType = (status: string) => {
  return status === '审核中' ? 'warning' : 'success'
}

const viewDetail = (item: any) => {
  router.push({
    path: `coursemanage/detail/${item.id}`,
    query: { course: JSON.stringify(item) }
  })
}

const approve = (item: any) => {
  item.status = '已通过'
}
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
</style>