<template>
  <div class="dynamic-detail">
    <el-button @click="router.push('/userhome/industrydynamic/list')">
      <el-icon><ArrowLeft /></el-icon>
      返回列表
    </el-button>

    <el-card v-if="dynamicData" class="mt-4">
      <h2>{{ dynamicData.title }}</h2>
      <div class="meta">
        <span><el-icon><User /></el-icon> 作者: {{ dynamicData.author }}</span>
        <span><el-icon><Clock /></el-icon> 时间: {{ dynamicData.createTime }}</span>
        <el-tag :type="dynamicData.status === '已发布' ? 'success' : 'warning'">
          {{ dynamicData.status }}
        </el-tag>
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

interface Dynamic {
  id: number
  title: string
  author: string
  content: string
  createTime: string
  status: string
}

const route = useRoute()
const router = useRouter()
const dynamicData = ref<Dynamic | null>(null)
const loading = ref(false)
// 调试输出
console.log('路由参数:', route.params)

// 模拟数据 - 确保与列表数据一致
const mockData = [
  {
    id: 1,
    title: '行业最新动态：AI技术在教育领域的应用',
    author: '张三',
    content: `
      <h3>人工智能教育应用详情</h3>
      <p>本文详细介绍了AI技术如何改变传统教育模式：</p>
      <ul>
        <li>智能个性化学习系统</li>
        <li>自动化作业批改</li>
        <li>虚拟教学助手</li>
      </ul>
      <p>实际案例显示，采用AI技术的班级平均成绩提升25%</p>
    `,
    createTime: '2025-06-15 14:30:00',
    status: '已发布'
  }
]

onMounted(() => {
  const id = Number(route.params.id)
  console.log('正在查找ID:', id)

  const item = mockData.find(item => item.id == id)
  console.log('找到的详情数据:', item)

  if (item) {
    dynamicData.value = item
  } else {
    ElMessage.error('未找到该动态详情')
    router.push('/industry-dynamic/list')
  }
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