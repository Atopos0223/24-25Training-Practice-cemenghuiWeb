<template>
  <div class="audit-courses">
    <div class="page-header">
      <h2>审核课程</h2>
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
      <el-table :data="filteredCourses" style="width: 100%" v-loading="loading">
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
            <el-tag type="warning">审核中</el-tag>
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

        <el-table-column prop="submitTime" label="提交时间" width="180" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="approveCourse(scope.row)"
			
            >
              通过
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="rejectCourse(scope.row)"

            >
              拒绝
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
            <span>状态：审核中</span>
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

    <!-- 审核对话框 -->
    <el-dialog v-model="showAuditDialog" :title="isApprove ? '通过审核' : '拒绝审核'" width="500px">
      <el-form :model="auditForm" ref="auditFormRef" label-width="100px">
        <el-form-item 
          label="审核意见" 
          prop="comment"
          :rules="!isApprove ? [{ required: true, message: '拒绝原因不能为空', trigger: 'blur' }] : []"
        >
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="isApprove ? '请输入通过意见（可选）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAuditDialog = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Picture, VideoCameraFilled } from '@element-plus/icons-vue'
import axios from 'axios'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showAuditDialog = ref(false)
const selectedCourse = ref(null)
const isApprove = ref(true)
const auditFormRef = ref()
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')

// 课程数据
const courseList = ref([])

// 审核表单
const auditForm = reactive({
  comment: ''
})

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

// 方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    return new Date(dateString).toLocaleString()
  } catch {
    return dateString
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
  
  currentVideoUrl.value = course.videoUrl
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

const approveCourse = (course) => {
  isApprove.value = true
  selectedCourse.value = course
  auditForm.comment = ''
  showAuditDialog.value = true
}

const rejectCourse = (course) => {
  isApprove.value = false
  selectedCourse.value = course
  auditForm.comment = ''
  showAuditDialog.value = true
}

const submitAudit = async () => {
  try {
    // 验证表单
    await auditFormRef.value.validate()
    
    loading.value = true
    const course = selectedCourse.value
    const newStatus = isApprove.value ? '已发布' : '未通过'
    
    const params = new URLSearchParams();
    params.append('id', selectedCourse.value.id);
    params.append('action', isApprove.value ? "pass" : "reject");
    params.append('comment', auditForm.comment);

    const response = await axios.post('/api/course/audit', params);

    if (response.data.success) {
      // 从列表中移除已审核的课程
      courseList.value = courseList.value.filter(c => c.id !== course.id)
      total.value = courseList.value.length
      
      ElMessage.success(`审核${isApprove.value ? '通过' : '拒绝'}成功`)
      showAuditDialog.value = false
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    if (error instanceof Error && !error.message.includes('validate')) {
      ElMessage.error('操作失败，请重试')
      console.error('审核课程失败:', error)
    }
  } finally {
    loading.value = false
  }
}

const loadCourses = async () => {
  loading.value = true
  try {
    // 只获取状态为"审核中"的课程
    const response = await axios.get('/api/course/list/auditing')
    if (response.data.success) {
      courseList.value = response.data.data.map(course => ({
        ...course,
        coverUrl: course.coverUrl ? processMediaUrl('http://localhost:8080'+course.coverUrl) : undefined,
        videoUrl: course.videoUrl ? processMediaUrl('http://localhost:8080'+course.videoUrl) : undefined,
        status: '审核中' // 确保状态为审核中
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

const processMediaUrl = (url) => {
  if (!url) return undefined;
  
  if (url.startsWith('http') || url.startsWith('data:')) return url;
  
  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || window.location.origin;
  
  return url.startsWith('/uploads/') 
    ? `${baseUrl}${url}`
    : `${baseUrl}/uploads${url.startsWith('/') ? '' : '/'}${url}`;
}

// 生命周期
onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.audit-courses {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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