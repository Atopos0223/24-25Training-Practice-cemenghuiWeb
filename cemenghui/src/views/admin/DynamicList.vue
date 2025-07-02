<template>
  <div class="dynamic-list">
    <div class="action-bar">
      <el-input
        v-model="searchKey"
        placeholder="请输入关键字搜索"
        style="width: 300px"
      />
      <el-button 
        type="primary" 
        @click="router.push('/publish')"
      >
        发布新动态
      </el-button>
    </div>

    <el-table :data="filteredData" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="createTime" label="发布时间" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button 
            type="text" 
            @click="viewDetail(row.id)"
          >
            查看
          </el-button>
          <el-button 
            type="text" 
            @click="editItem(row)"
            v-if="row.create_id === userId && row.status !== '已发布'"
          >
            编辑
          </el-button>
          <el-button 
            type="text" 
            @click="deleteItem(row)"
            v-if="row.create_id === userId && row.status !== '已发布'"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="data.length"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

interface Dynamic {
  id: number
  title: string
  author: string
  createTime: string
  status: string
  create_id: number
}

const router = useRouter()
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const userId = userInfo.id

const data = ref<Dynamic[]>([])

onMounted(async () => {
  const res = await request.get('http://localhost:8080/getAllNews')
  // 假设返回的数据结构为 { data: [...] }
  data.value = res.data
})

const filteredData = computed(() => {
  return data.value.filter(item => 
    item.title.includes(searchKey.value) || 
    item.author.includes(searchKey.value)
  )
})

const viewDetail = (id: any) => {
  router.push(`/industry-dynamic/detail/${id}`);
};

const editItem = (row: Dynamic) => {
  router.push({
    path: '/industry-dynamic/publish',
    query: { editId: row.id }
  })
}

const deleteItem = (row: Dynamic) => {
  data.value = data.value.filter(item => item.id !== row.id)
}
</script>

<style scoped>
.dynamic-list {
  padding: 20px;
}
.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>