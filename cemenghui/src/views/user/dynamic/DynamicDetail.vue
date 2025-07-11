<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><TrendCharts /></el-icon> 动态详情</h2>
    <el-divider />
    <div class="dynamic-detail">
      <el-button @click="goBack">
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
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Clock, TrendCharts } from '@element-plus/icons-vue'
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

const goBack = () => {
  if (route.path.startsWith('/adminhome')) {
    router.push('/adminhome/manage-dynamics')
  } else {
    router.push('/userhome/industrydynamic/list')
  }
}

onMounted(() => {
  fetchDetail()
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