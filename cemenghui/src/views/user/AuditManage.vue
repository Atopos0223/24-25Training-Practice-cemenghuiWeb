<template>
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
.audit-manage {
  padding: 20px;
}
</style>