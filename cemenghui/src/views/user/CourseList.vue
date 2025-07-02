<template>
  <div class="course-list">
    <div class="action-bar">
      <el-input v-model="searchKey" placeholder="搜索课程" style="width: 300px" />
      <el-button type="primary" @click="goToAdd">添加课程</el-button>
    </div>

    <el-table :data="filteredCourses" border v-loading="loading">
      <el-table-column prop="title" label="课程名称" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="status" label="状态">
        <template #default="{row}">
          <el-tag :type="getStatusTagType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
	    <el-table-column prop="createTime" label="发布时间" width="180">
	      <template #default="{row}">
	        {{ formatDate(row.createTime) }}
	      </template>
	    </el-table-column>
      <el-table-column label="封面" width="120">
        <template #default="{row}">
          <div class="cover-container">
            <el-image 
              :src="getFinalCoverUrl(row.coverUrl)"
              :preview-src-list="[getFinalCoverUrl(row.coverUrl)]"
              style="width: 80px; height: 45px"
              fit="cover"
              @error="handleImageError(row.id, row.coverUrl)"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                  <span>封面加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="视频" width="120">
        <template #default="{row}">
          <div class="video-container">
            <el-button 
              type="primary" 
              size="small" 
              @click="previewVideo(row)"
              :disabled="!row.videoUrl"
            >
              {{ row.videoUrl ? '预览视频' : '无视频' }}
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
          <el-button type="warning" size="small" @click="editCourse(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteCourse(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 视频预览对话框 -->
    <el-dialog v-model="videoDialogVisible" title="视频预览" width="70%">
      <video 
        v-if="currentVideoUrl"
        controls
        style="width: 100%"
        :src="currentVideoUrl"
        @error="handleVideoError"
      >
        您的浏览器不支持视频播放
      </video>
      <div v-else class="video-error">
        <el-icon><VideoCameraFilled /></el-icon>
        <span>视频加载失败或不存在</span>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, VideoCameraFilled } from '@element-plus/icons-vue'
import axios from 'axios'

interface Course {
  id: number
  title: string
  author: string
  status: string
  coverUrl?: string
  intro?: string
  videoUrl?: string
  createTime: string
}

const router = useRouter()
const searchKey = ref('')
const currentCourse = ref<Course | null>(null)
const courses = ref<Course[]>([])
const loading = ref(false)
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')

const filteredCourses = computed(() => {
  const key = searchKey.value.toLowerCase()
  return courses.value.filter(c => 
    c.title.toLowerCase().includes(key) || 
    c.author.toLowerCase().includes(key)
  ) 
})

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    '已发布': 'success',
    '审核中': 'warning',
    '未通过': 'danger'
  }
  return map[status] || ''
}

const viewDetail = (id: number) => {
  router.push(`/userhome/coursemanage/detail/${id}`)
}

const editCourse = (course: Course) => {
  router.push(`/userhome/coursemanage/edit/${course.id}`)
}

const deleteCourse = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该课程吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await axios.delete(`/api/course/${id}`)
    if (res.data.success) {
      ElMessage.success('删除成功')
      await loadCourses()
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      ElMessage.error(`删除失败: ${error.response?.data?.message || error.message}`)
    } else if (error instanceof Error) {
      if (!error.message.includes('cancel')) {
        ElMessage.error(`删除失败: ${error.message}`)
      }
    } else if (typeof error === 'string') {
      ElMessage.error(`删除失败: ${error}`)
    } else {
      ElMessage.error('删除失败: 未知错误')
    }
  }
}

const goToAdd = () => {
  router.push('/userhome/coursemanage/add')
}

const loadCourses = async () => {
  loading.value = true;
  try {
    const res = await axios.get<{ 
      success: boolean; 
      data: Course[];
      message?: string;
    }>('/api/course/list');
    
    if (res.data.success) {
      courses.value = res.data.data.map(course => ({
        ...course,
        coverUrl: course.coverUrl ? processMediaUrl('http://localhost:8080'+course.coverUrl) : undefined,
        videoUrl: course.videoUrl ? processMediaUrl('http://localhost:8080'+course.videoUrl) : undefined
      }));
    } else {
      ElMessage.error(res.data.message || '加载课程列表失败');
    }
  } catch (error: unknown) {
    let errorMessage = '请求失败';
    if (error instanceof Error) {
      errorMessage += ': ' + error.message;
    } else if (typeof error === 'string') {
      errorMessage += ': ' + error;
    }
    ElMessage.error(errorMessage);
  } finally {
    loading.value = false;
  }
};

// 统一处理媒体URL（封面和视频）
const processMediaUrl = (url: string | undefined): string | undefined => {
  if (!url) return undefined;
  
  // 已经是完整URL或base64数据
  if (url.startsWith('http') || url.startsWith('data:')) return url;
  
  // 获取基础URL（优先使用环境变量）
  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || 
                 window.location.origin;
  
  // 处理/uploads开头的路径
  return url.startsWith('/uploads/') 
    ? `${baseUrl}${url}`
    : `${baseUrl}/uploads${url.startsWith('/') ? '' : '/'}${url}`;
};

const getFinalCoverUrl = (url: string | undefined): string => {
  if (!url) {
    console.warn('封面URL为空，使用默认封面');
    return getDefaultCoverUrl();
  }

  if (url.startsWith('http') || url.startsWith('data:')) {
    return url;
  }

  return processMediaUrl(url) || getDefaultCoverUrl();
};

const getDefaultCoverUrl = () => {
  return `${window.location.origin}/default-cover.jpg`;
};

const handleImageError = (courseId: number, originalUrl: string | undefined) => {
  console.error('封面加载失败', {
    courseId,
    originalUrl,
    processedUrl: getFinalCoverUrl(originalUrl),
    timestamp: new Date().toISOString()
  });
};

// 视频相关方法
const previewVideo = (course: Course) => {
  if (!course.videoUrl) {
    ElMessage.warning('该课程没有视频内容');
    return;
  }
  
  currentVideoUrl.value = course.videoUrl;
  videoDialogVisible.value = true;
};

const handleVideoError = () => {
  ElMessage.error('视频加载失败，请检查视频地址');
  currentVideoUrl.value = '';
};
const formatDate = (dateString: string) => {
  if (!dateString) return '-';
  try {
    return new Date(dateString).toLocaleString();
  } catch {
    return dateString; // 如果格式化失败，返回原始字符串
  }
};

onMounted(() => {
  loadCourses();
});
</script>

<style scoped>
.course-list {
  padding: 20px;
}
.action-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.cover-container, .video-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
.image-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--el-text-color-secondary);
}
.video-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--el-color-danger);
  padding: 20px;
}
</style>