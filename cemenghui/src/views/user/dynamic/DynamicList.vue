<template>

  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><TrendCharts /></el-icon> 动态列表</h2>
    <el-divider />
    <div class="dynamic-list">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入关键字搜索"
        class="mb-4"
        style="width: 300px"
        @input="fetchList"
        clearable
      />
      <el-table :data="newsList" style="width: 100%" border>
        <el-table-column prop="id" label="序号" width="80" />
        <el-table-column prop="title" label="标题" min-width="120" />
        <el-table-column prop="author_id" label="作者ID" width="100" />
        <el-table-column prop="create_time" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.create_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span>{{ row.status === 1 ? '已发布' : row.status === 0 ? '审核中' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360">
          <template #default="{row}">
            <div class="button-row">
              <el-button type="primary" size="small" @click="viewDetail(row)">查看</el-button>
              <el-button type="warning" size="small" @click="editDynamic(row)" v-if="row.canEdit">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteDynamic(row)" v-if="row.canEdit">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="mt-4"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </div>
  </el-card>

</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const newsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const router = useRouter()
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const userId = userInfo.id

const fetchList = async () => {
  try {
    const res = await request.get('/news/all', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    })
    if (res.data.code === 200) {
      newsList.value = res.data.data.records || res.data.data || []
      total.value = res.data.data.total || newsList.value.length
    } else {
      ElMessage.error(res.data.msg || '获取动态列表失败')
    }
  } catch (e) {
    ElMessage.error('获取动态列表失败')
  }
}

const handleView = (row) => {
  router.push({ name: 'DynamicDetail', params: { id: row.id } })
}

const handleEdit = (row) => {
  router.push({ name: 'EditDynamic', params: { id: row.id } })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除动态《${row.title}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/news/delete/${row.id}`)
      if (res.data.code === 200) {
        ElMessage.success('删除成功')
        fetchList()
      } else {
        ElMessage.error(res.data.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

function formatDateTime(val: string) {
  if (!val) return '';
  return val.replace('T', ' ').replace(/\..*$/, '');
}

onMounted(() => {
  fetchList()
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