<template>
  <div class="audit-news">
    <div class="page-header">
      <h2>审核资讯</h2>
      <div class="filter-section">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索标题或作者"
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
      <el-table :data="filteredNews" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column prop="auditTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              size="small" 
              type="success" 
              @click="approveNews(scope.row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              size="small" 
              type="danger" 
              @click="rejectNews(scope.row)"
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
    <el-dialog v-model="showDetailDialog" title="资讯详情" width="800px">
      <div v-if="selectedNews" class="news-detail">
        <div class="detail-header">
          <h3>{{ selectedNews.title }}</h3>
          <div class="meta-info">
            <span>作者：{{ selectedNews.author }}</span>
            <span>分类：{{ selectedNews.category }}</span>
            <span>发布时间：{{ selectedNews.createTime }}</span>
          </div>
        </div>
        <div class="detail-content" v-html="selectedNews.content"></div>
        <div v-if="selectedNews.auditComment" class="audit-comment">
          <h4>审核意见：</h4>
          <p>{{ selectedNews.auditComment }}</p>
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
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showAuditDialog = ref(false)
const selectedNews = ref(null)
const auditAction = ref('')
const auditFormRef = ref()

// 资讯数据
const newsList = ref([
  {
    id: 1,
    title: '人工智能在测试行业的应用前景',
    author: '张三',
    category: '技术动态',
    status: 'pending',
    createTime: '2025-01-15 10:30:00',
    auditTime: '',
    content: '随着人工智能技术的快速发展，AI在软件测试领域的应用越来越广泛...',
    auditComment: ''
  },
  {
    id: 2,
    title: '自动化测试工具对比分析',
    author: '李四',
    category: '工具评测',
    status: 'approved',
    createTime: '2025-01-14 15:20:00',
    auditTime: '2025-01-15 09:00:00',
    content: '本文对比了市面上主流的自动化测试工具，包括Selenium、Appium等...',
    auditComment: '内容详实，分析到位'
  },
  {
    id: 3,
    title: '测试团队管理最佳实践',
    author: '王五',
    category: '管理经验',
    status: 'rejected',
    createTime: '2025-01-13 14:10:00',
    auditTime: '2025-01-14 16:30:00',
    content: '分享测试团队管理的经验和教训...',
    auditComment: '内容过于简单，缺乏深度'
  }
])

// 审核表单
const auditForm = reactive({
  comment: ''
})

// 计算属性
const filteredNews = computed(() => {
  let filtered = newsList.value

  if (statusFilter.value) {
    filtered = filtered.filter(news => news.status === statusFilter.value)
  }

  if (searchKeyword.value) {
    filtered = filtered.filter(news => 
      news.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      news.author.toLowerCase().includes(searchKeyword.value.toLowerCase())
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

const viewDetail = (news) => {
  selectedNews.value = news
  showDetailDialog.value = true
}

const approveNews = (news) => {
  auditAction.value = 'approve'
  selectedNews.value = news
  auditForm.comment = ''
  showAuditDialog.value = true
}

const rejectNews = (news) => {
  auditAction.value = 'reject'
  selectedNews.value = news
  auditForm.comment = ''
  showAuditDialog.value = true
}

const submitAudit = async () => {
  if (auditAction.value === 'reject' && !auditForm.comment.trim()) {
    ElMessage.warning('拒绝审核时必须填写拒绝原因')
    return
  }

  try {
    // 更新资讯状态
    const news = selectedNews.value
    news.status = auditAction.value === 'approve' ? 'approved' : 'rejected'
    news.auditTime = new Date().toLocaleString()
    news.auditComment = auditForm.comment

    ElMessage.success(`审核${auditAction.value === 'approve' ? '通过' : '拒绝'}成功`)
    showAuditDialog.value = false
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 生命周期
onMounted(() => {
  total.value = newsList.value.length
})
</script>

<style scoped>
.audit-news {
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

.news-detail {
  max-height: 500px;
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

.detail-content {
  line-height: 1.6;
  color: #606266;
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