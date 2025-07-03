<template>
  <div class="manage-courses">
    <div class="page-header">
      <h2>管理课程</h2>
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或作者"
          style="width: 250px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <el-card>
      <el-table :data="paginatedCourses" style="width: 100%" v-loading="loading">
        <el-table-column 
          type="index"
          label="序号"
          width="80"
          :index="(index) => index + 1"
        />
        <el-table-column prop="title" label="课程名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="getStatusTagType(scope.row.status)"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="封面" width="120">
          <template #default="scope">
            <el-image 
              :src="getFinalCoverUrl(scope.row.coverUrl)"
              :preview-src-list="[getFinalCoverUrl(scope.row.coverUrl)]"
              style="width: 80px; height: 45px"
              fit="cover"
              @error="handleImageError(scope.row.id, scope.row.coverUrl)"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                  <span>封面加载失败</span>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="视频" width="120">
          <template #default="scope">
            <div class="video-container">
              <el-button 
                type="primary" 
                size="small" 
                @click="previewVideo(scope.row)"
                :disabled="!scope.row.videoUrl"
              >
                {{ scope.row.videoUrl ? '预览视频' : '无视频' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="editCourse(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteCourse(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

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

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="课程详情" width="800px">
      <div v-if="selectedCourse" class="course-detail">
        <div class="detail-header">
          <h3>{{ selectedCourse.title }}</h3>
          <div class="meta-info">
            <span>作者：{{ selectedCourse.author }}</span>
            <span>状态：{{ selectedCourse.status }}</span>
            <span>提交时间：{{ formatDate(selectedCourse.createTime) }}</span>
          </div>
          <div v-if="selectedCourse.coverUrl" class="cover-preview">
            <el-image 
              :src="getFinalCoverUrl(selectedCourse.coverUrl)"
              :preview-src-list="[getFinalCoverUrl(selectedCourse.coverUrl)]"
              style="width: 200px; height: 120px"
              fit="cover"
            />
          </div>
        </div>
        <div v-if="selectedCourse.intro" class="detail-section">
          <h4>课程简介</h4>
          <p>{{ selectedCourse.intro }}</p>
        </div>
        <div v-if="selectedCourse.videoUrl" class="detail-section">
          <h4>课程视频</h4>
          <el-button 
            type="primary" 
            @click="previewVideo(selectedCourse)"
          >
            预览视频
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Picture, VideoCameraFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const selectedCourse = ref(null)
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')
const router = useRouter()
// 课程数据
const courseList = ref([])

// 计算属性
const filteredCourses = computed(() => {
  if (!searchKeyword.value) {
    return courseList.value
  }

  return courseList.value.filter(course => 
    course.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    course.author.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})

// 方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    return new Date(dateString).toLocaleString()
  } catch {
    return dateString
  }
}

const getStatusTagType = (status) => {
  switch (status) {
    case '已发布': return 'success'
    case '未通过': return 'danger'
    default: return 'info'
  }
}

const getFinalCoverUrl = (url) => {
  if (!url) {
    console.warn('封面URL为空，使用默认封面')
    return getDefaultCoverUrl()
  }

  if (url.startsWith('http') || url.startsWith('data:')) {
    return url
  }

  // 处理相对路径
  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || window.location.origin
  return url.startsWith('/uploads/') 
    ? `${baseUrl}${url}`
    : `${baseUrl}/uploads${url.startsWith('/') ? '' : '/'}${url}`
}

const getDefaultCoverUrl = () => {
  return `${window.location.origin}/default-cover.jpg`
}

const handleImageError = (courseId, originalUrl) => {
  console.error('封面加载失败', {
    courseId,
    originalUrl,
    processedUrl: getFinalCoverUrl(originalUrl),
    timestamp: new Date().toISOString()
  })
}

const previewVideo = (course) => {
  if (!course.videoUrl) {
    ElMessage.warning('该课程没有视频内容')
    return
  }
  
  currentVideoUrl.value = processMediaUrl(course.videoUrl)
  videoDialogVisible.value = true
}

const handleVideoError = () => {
  ElMessage.error('视频加载失败，请检查视频地址')
  currentVideoUrl.value = ''
  videoDialogVisible.value = false
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const viewDetail = (course) => {
  selectedCourse.value = course
  showDetailDialog.value = true
}

const editCourse = (course) => {
  router.push(`/adminhome/courses-management/edit/${course.id}`)
}
  
// 修改deleteCourse方法
const deleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm(`确定删除课程 "${course.title}" 吗？`, '提示', {
      type: 'warning'
    });
    
    loading.value = true;
    // 修正删除接口路径
    const response = await axios.delete(`/api/course/${course.id}`);
    
    if (response.data.success) {
      ElMessage.success('删除成功');
      courseList.value = courseList.value.filter(c => c.id !== course.id);
      total.value = courseList.value.length;
    } else {
      ElMessage.error(response.data.message || '删除失败');
    }
  } catch (error) {
    if (!error.toString().includes('cancel')) {
      ElMessage.error('删除失败');
      console.error('删除课程失败:', error);
    }
  } finally {
    loading.value = false;
  }
};

const processMediaUrl = (url) => {
  if (!url) return undefined;
  
  if (url.startsWith('http') || url.startsWith('data:')) return url;
  
  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || window.location.origin;
  
  return url.startsWith('/uploads/') 
    ? `${baseUrl}${url}`
    : `${baseUrl}/uploads${url.startsWith('/') ? '' : '/'}${url}`;
}

const loadCourses = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/course/list/managed')
    if (response.data.success) {
      courseList.value = response.data.data.map(course => ({
        ...course,
        coverUrl: course.coverUrl ? processMediaUrl('http://localhost:8080'+course.coverUrl) : undefined,
        videoUrl: course.videoUrl ? processMediaUrl('http://localhost:8080'+course.videoUrl) : undefined
      }))
      total.value = courseList.value.length
    } else {
      ElMessage.error(response.data.message || '加载课程列表失败')
    }
  } catch (error) {
    ElMessage.error('加载课程列表失败')
    console.error('加载课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.manage-courses {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.filter-section {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.course-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-header h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.meta-info {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.detail-section p {
  line-height: 1.6;
  color: #606266;
  margin: 0;
}

.cover-preview {
  margin: 15px 0;
}

.image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 80px;
  height: 45px;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.video-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--el-color-danger);
  padding: 20px;
}
</style>