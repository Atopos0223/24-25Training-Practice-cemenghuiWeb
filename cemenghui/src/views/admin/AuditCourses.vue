<template>
  <div class="audit-courses">
    <div class="page-header">
      <h2>审核课程</h2>
      <div class="filter-section">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-select v-model="categoryFilter" placeholder="分类筛选" style="width: 120px; margin-right: 10px">
          <el-option label="全部" value="" />
          <el-option label="功能测试" value="功能测试" />
          <el-option label="性能测试" value="性能测试" />
          <el-option label="自动化测试" value="自动化测试" />
          <el-option label="安全测试" value="安全测试" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或讲师"
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="课程名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="instructor" label="讲师" width="120" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="duration" label="时长" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            <span v-if="scope.row.price > 0">¥{{ scope.row.price }}</span>
            <span v-else class="free-tag">免费</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              size="small" 
              type="success" 
              @click="approveCourse(scope.row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
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

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="课程详情" width="800px">
      <div v-if="selectedCourse" class="course-detail">
        <div class="detail-header">
          <h3>{{ selectedCourse.title }}</h3>
          <div class="meta-info">
            <span>讲师：{{ selectedCourse.instructor }}</span>
            <span>分类：{{ selectedCourse.category }}</span>
            <span>时长：{{ selectedCourse.duration }}</span>
            <span>价格：{{ selectedCourse.price > 0 ? `¥${selectedCourse.price}` : '免费' }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>课程简介</h4>
          <p>{{ selectedCourse.description }}</p>
        </div>
        <div class="detail-section">
          <h4>课程大纲</h4>
          <div class="outline-list">
            <div v-for="(item, index) in selectedCourse.outline" :key="index" class="outline-item">
              <span class="outline-number">{{ index + 1 }}</span>
              <span class="outline-title">{{ item.title }}</span>
              <span class="outline-duration">{{ item.duration }}</span>
            </div>
          </div>
        </div>
        <div v-if="selectedCourse.auditComment" class="audit-comment">
          <h4>审核意见：</h4>
          <p>{{ selectedCourse.auditComment }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="showAuditDialog" :title="auditAction === 'approve' ? '通过审核' : '拒绝审核'" width="500px">
      <el-form :model="auditForm" ref="auditFormRef" label-width="100px">
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="auditAction === 'approve' ? '请输入通过意见（可选）' : '请输入拒绝原因'"
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
import { Search } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const categoryFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showAuditDialog = ref(false)
const selectedCourse = ref(null)
const auditAction = ref('')
const auditFormRef = ref()

// 课程数据
const courseList = ref([
  {
    id: 1,
    title: 'Selenium自动化测试实战',
    instructor: '张老师',
    category: '自动化测试',
    duration: '20小时',
    price: 299,
    status: 'pending',
    submitTime: '2025-01-15 10:30:00',
    auditTime: '',
    description: '本课程将带你深入学习Selenium自动化测试框架，从基础概念到实战项目，全面掌握Web自动化测试技能。',
    outline: [
      { title: 'Selenium基础介绍', duration: '2小时' },
      { title: '元素定位方法', duration: '3小时' },
      { title: '等待机制', duration: '2小时' },
      { title: '测试框架搭建', duration: '4小时' },
      { title: '实战项目', duration: '9小时' }
    ],
    auditComment: ''
  },
  {
    id: 2,
    title: '性能测试入门指南',
    instructor: '李老师',
    category: '性能测试',
    duration: '15小时',
    price: 0,
    status: 'approved',
    submitTime: '2025-01-14 15:20:00',
    auditTime: '2025-01-15 09:00:00',
    description: '从零开始学习性能测试，掌握JMeter等工具的使用，了解性能测试的核心概念和方法。',
    outline: [
      { title: '性能测试基础', duration: '3小时' },
      { title: 'JMeter工具使用', duration: '6小时' },
      { title: '性能分析', duration: '4小时' },
      { title: '实战案例', duration: '2小时' }
    ],
    auditComment: '课程内容完整，适合初学者'
  },
  {
    id: 3,
    title: '移动端测试实践',
    instructor: '王老师',
    category: '功能测试',
    duration: '18小时',
    price: 199,
    status: 'rejected',
    submitTime: '2025-01-13 14:10:00',
    auditTime: '2025-01-14 16:30:00',
    description: '移动端测试的完整流程和方法...',
    outline: [
      { title: '移动端测试概述', duration: '2小时' },
      { title: '测试工具使用', duration: '8小时' },
      { title: '测试用例设计', duration: '6小时' },
      { title: '实战演练', duration: '2小时' }
    ],
    auditComment: '课程内容需要补充更多实际案例'
  }
])

// 审核表单
const auditForm = reactive({
  comment: ''
})

// 计算属性
const filteredCourses = computed(() => {
  let filtered = courseList.value

  if (statusFilter.value) {
    filtered = filtered.filter(course => course.status === statusFilter.value)
  }

  if (categoryFilter.value) {
    filtered = filtered.filter(course => course.category === categoryFilter.value)
  }

  if (searchKeyword.value) {
    filtered = filtered.filter(course => 
      course.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.instructor.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  return filtered
})

// 方法
const getStatusType = (status) => {
  const statusTypes = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusTypes[status] || 'info'
}

const getStatusText = (status) => {
  const statusTexts = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return statusTexts[status] || '未知'
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
  auditAction.value = 'approve'
  selectedCourse.value = course
  auditForm.comment = ''
  showAuditDialog.value = true
}

const rejectCourse = (course) => {
  auditAction.value = 'reject'
  selectedCourse.value = course
  auditForm.comment = ''
  showAuditDialog.value = true
}

const submitAudit = async () => {
  if (auditAction.value === 'reject' && !auditForm.comment.trim()) {
    ElMessage.warning('拒绝审核时必须填写拒绝原因')
    return
  }

  try {
    // 更新课程状态
    const course = selectedCourse.value
    course.status = auditAction.value === 'approve' ? 'approved' : 'rejected'
    course.auditTime = new Date().toLocaleString()
    course.auditComment = auditForm.comment

    ElMessage.success(`审核${auditAction.value === 'approve' ? '通过' : '拒绝'}成功`)
    showAuditDialog.value = false
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 生命周期
onMounted(() => {
  total.value = courseList.value.length
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

.free-tag {
  color: #67c23a;
  font-weight: bold;
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

.outline-list {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.outline-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.outline-item:last-child {
  border-bottom: none;
}

.outline-number {
  width: 30px;
  height: 30px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  margin-right: 15px;
}

.outline-title {
  flex: 1;
  color: #303133;
}

.outline-duration {
  color: #909399;
  font-size: 14px;
}

.audit-comment {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.audit-comment h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 