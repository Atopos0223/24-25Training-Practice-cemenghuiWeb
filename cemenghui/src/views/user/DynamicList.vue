<template>
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
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author_id" label="作者ID" />
      <el-table-column prop="create_time" label="发布时间">
        <template #default="{ row }">
          {{ formatDateTime(row.create_time) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <span>{{ row.status === 1 ? '已发布' : row.status === 0 ? '审核中' : '未知' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="text" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.author_id === userId" type="text" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.author_id === userId" type="text" @click="handleDelete(row)" style="color: red;">删除</el-button>
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
.dynamic-list {
  padding: 30px 40px;
  background: #f7f9fb;
  border-radius: 8px;
  max-width: 1100px;
  margin: 30px auto;
}
</style>