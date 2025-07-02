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
        @click="router.push('/userhome/industrydynamic/publish')"
      >
        发布新动态
      </el-button>
    </div>

    <el-table :data="filteredData" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者ID" />
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
            v-if="row.status !== '已发布'"
          >
            编辑
          </el-button>
          <el-button 
            type="text" 
            @click="deleteItem(row)"
            v-if="row.status !== '已发布'"
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
import axios from 'axios'

interface Dynamic {
  id: number
  title: string
  author_id: number
  createTime: string
  status: string
}

const router = useRouter()
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const data = ref<Dynamic[]>([])

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/news/all')
    // 字段映射处理
    data.value = res.data.data.map((item: any) => ({
      id: item.id,
      title: item.title || '',
      author_id: item.author_id || '未知', 
      createTime: item.create_time || '',
      status: item.status === 1 ? '已发布' : (item.status === 0 ? '审核中' : item.status)
    }))
  } catch (error) {
    console.error('获取新闻失败', error)
  }
})

// 定义方法
const viewDetail = (id: any) => {
  router.push(`/userhome/industrydynamic/detail/${id}`);
};

const filteredData = computed(() => {
  return data.value.filter(item => 
    item.title.includes(searchKey.value) || 
    item.author.includes(searchKey.value)
  )
})

const editItem = (row: Dynamic) => {
  router.push({
    path: '/userhome/industrydynamic/publish',
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