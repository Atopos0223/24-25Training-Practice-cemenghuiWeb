<template>
  <div class="dynamic-detail">
    <el-button @click="router.push('/userhome/industrydynamic/list')">
      <el-icon><ArrowLeft /></el-icon>
      返回列表
    </el-button>

    <el-card v-if="dynamicData" class="mt-4">
      <h2>{{ dynamicData.title }}</h2>
      <div style="margin-bottom: 16px;">
        <span>
          <el-icon><User /></el-icon>
          作者ID：{{ dynamicData.author_id }}
        </span>
        <span style="margin-left: 24px;">
          <el-icon><Clock /></el-icon>
          发布时间：{{ formatDateTime(dynamicData.create_time) }}
        </span>
      </div>
      <div v-if="dynamicData.image" style="margin-bottom: 16px;">
        <!-- 正确写法：使用 v-bind 绑定 src，拼接完整 URL 并包裹动态数据 -->
        <img :src="`http://localhost:8080${dynamicData.image}`" alt="新闻图片" style="max-width: 300px;" />
      </div>
      <el-divider />
      <div class="content" v-html="dynamicData.content"></div>
    </el-card>

    <el-empty v-else description="动态内容加载失败" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Clock } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface Dynamic {
  id: number
  title: string
  author: string
  content: string
  createTime: string
  status: string
  author_id: number
  create_time: string
  image?: string
}

const route = useRoute()
const router = useRouter()
const dynamicData = ref<Dynamic | null>(null)
const loading = ref(false)
// 调试输出
console.log('路由参数:', route.params)

function formatDateTime(val: string) {
  if (!val) return '';
  return val.replace('T', ' ').replace(/\..*$/, '');
}

const fetchDetail = async () => {
  try {
    const res = await request.get(`/news/detail/${route.params.id}`)
    if (res.data.code === 200) {
      dynamicData.value = res.data.data
    } else {
      ElMessage.error(res.data.msg || '获取详情失败')
    }
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.dynamic-detail {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.meta {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 15px;
  color: #666;
}
.content {
  line-height: 1.8;
}
.content :deep(h3) {
  margin: 20px 0 15px;
  color: #333;
}
.content :deep(ul) {
  padding-left: 20px;
}
</style>