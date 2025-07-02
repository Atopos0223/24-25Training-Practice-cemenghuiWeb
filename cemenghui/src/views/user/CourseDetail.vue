<template>
  <div class="course-detail-container">
    <!-- 返回按钮 - 修复点击卡死问题 -->
    <el-button 
      type="primary" 
      @click="handleBack"
      class="back-btn"
    >
      <el-icon><ArrowLeft /></el-icon>
      返回列表
    </el-button>

    <!-- 主内容区域 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>课程加载中...</span>
    </div>

    <div v-else-if="course" class="course-content">
      <!-- 封面和基本信息 -->
      <div class="course-header">
        <el-image
          :src="getFinalUrl(course.coverUrl)"
          style="width: 300px; height: 180px;"
        >
          <template #error>
            <div>封面加载失败</div>
          </template>
        </el-image>

        <div class="course-meta">
          <h2 class="course-title">标题:{{ course.title || '未命名课程' }}</h2>
          <p class="course-author">作者: {{ course.author || '未知' }}</p>
          <el-tag :type="getStatusTagType(course.status)">
            {{ course.status || '未知状态' }}
          </el-tag>
          <p class="course-desc">简介:{{ course.intro || '暂无课程简介' }}</p>
        </div>
      </div>

      <!-- 视频播放器 - 修复视频不显示问题 -->
    <div class="video">
      <h3>课程视频</h3>
      <video controls width="100%">
        <source :src="getFinalUrl(course.videoUrl)" type="video/mp4">
        您的浏览器不支持视频播放。
      </video>
    </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <el-alert
        title="课程加载失败"
        type="error"
        :closable="false"
        center
      >
        <el-button type="primary" @click="retryLoad">重新加载</el-button>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Picture, Loading } from '@element-plus/icons-vue'

interface Course {
  id: number
  title: string
  author: string
  status: string
  coverUrl?: string
  intro?: string
  videoUrl?: string
}

const route = useRoute()
const router = useRouter()
const course = ref<Course | null>(null)
const loading = ref(true)
const videoError = ref(false)

// 修复返回按钮卡死问题
const handleBack = () => {
  router.push('/userhome/coursemanage/list').catch(() => {
    // 防止重复导航错误
    console.log('导航已在进行中')
  })
}

// 加载课程数据
const loadCourse = async () => {
  try {
    loading.value = true
    videoError.value = false
    
    const id = route.params.id
    if (!id) throw new Error('缺少课程ID')

    const res = await axios.get(`/api/course/${id}`)
    
    if (res.data.success) {
      course.value = {
        ...res.data.data,
        // 确保URL格式正确
        coverUrl: fixUrl(res.data.data.coverUrl),
        videoUrl: fixUrl(res.data.data.videoUrl)
      }
    } else {
      throw new Error(res.data.message || '加载课程失败')
    }
  } catch (error: unknown) {
    console.error('加载课程详情失败:', error);
    ElMessage.error(error instanceof Error ? error.message : '加载课程失败');
    
    // 创建一个符合 Course 类型的错误状态对象
    course.value = {
        id: -1,
        title: "加载失败",
        author: "系统",
        status: "error",
        coverUrl: undefined,
        intro: "课程加载失败，请稍后重试",
        videoUrl: undefined
    };
} finally {
    loading.value = false;
}
}

const fixUrl = (url?: string): string | undefined => {
  if (!url) return undefined;
  
  // 开发环境使用完整URL（根据实际后端地址调整）
  const baseUrl = import.meta.env.DEV 
    ? 'http://localhost:8080'  // 确保与后端地址一致
    : window.location.origin;
  
  // 处理三种常见情况：
  // 1. 已经是完整URL => 直接返回
  // 2. 以/uploads开头 => 拼接baseUrl
  // 3. 其他情况 => 添加/uploads前缀
  if (url.startsWith('http') || url.startsWith('data:')) {
    return url;
  } else if (url.startsWith('/uploads/')) {
    return `${baseUrl}${url}`;
  } else {
    return `${baseUrl}/uploads/${url.replace(/^\/?uploads\//, '')}`;
  }
};

// 视频加载失败处理
const handleVideoError = () => {
  videoError.value = true
  ElMessage.warning('视频加载失败，请检查视频地址')
}

// 重新加载
const retryLoad = () => {
  loadCourse()
}

// 状态标签类型
const getStatusTagType = (status: string) => {
  return [status] || ''
}

const getFinalUrl = (url?: string): string => {
  if (!url) return '';
  
  // 已经是完整URL或base64数据
  if (url.startsWith('http') || url.startsWith('data:')) return url;
  
  // 获取基础URL
  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || 
                 window.location.origin;
  
  // 视频特殊处理（假设视频存储在/videos/目录）
  if (url.includes('/videos/') || url.includes('.mp4')) {
    return url.startsWith('/') 
      ? `${baseUrl}${url}`
      : `${baseUrl}/videos/${url}`;
  }
  
  // 默认处理（封面等资源）
  return url.startsWith('/') 
    ? `${baseUrl}${url}`
    : `${baseUrl}/uploads/${url}`;
};

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
.course-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.back-btn {
  margin-bottom: 20px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.loading-icon {
  font-size: 40px;
  margin-bottom: 15px;
  animation: rotate 2s linear infinite;
}

.course-header {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.course-cover {
  width: 300px;
  height: 200px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.course-meta {
  flex: 1;
}

.course-title {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 24px;
}

.video-player-wrapper {
  margin-top: 30px;
}

.video-player {
  width: 100%;
  max-height: 500px;
  background: #000;
  border-radius: 8px;
}

.video-error {
  color: #f56c6c;
  text-align: center;
}

.cover-error {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.error-state {
  margin-top: 50px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>