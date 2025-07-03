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
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="id" label="作者ID" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>

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
        <h2>{{ selectedNews.title }}</h2>
        <div style="margin-bottom: 16px;">
          <span>
            <el-icon><User /></el-icon>
            作者ID：{{ selectedNews.id }}
          </span>
          <span style="margin-left: 24px;">
            <el-icon><Clock /></el-icon>
            发布时间：{{ formatDateTime(selectedNews.createTime) }}
          </span>
        </div>
        <div v-if="selectedNews.image" style="margin-bottom: 16px;">
          <img :src="selectedNews.image.startsWith('http') ? selectedNews.image : 'http://localhost:8080' + selectedNews.image" alt="新闻图片" style="max-width: 300px;" />
        </div>
        <el-divider />
        <div class="content" v-html="selectedNews.content"></div>
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
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Search, User, Clock } from '@element-plus/icons-vue'

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
const newsList = ref([])

const fetchNewsList = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/news/all')
    const list = Array.isArray(res.data.data) ? res.data.data : []
    newsList.value = list.map(item => ({
      ...item,
      createTime: item.createTime || item.create_time || item.publishTime || item.time || '',
      status: item.status === 0 ? 'pending' : item.status === 1 ? 'approved' : 'rejected'
    }))
    total.value = newsList.value.length
  } catch (e) {
    ElMessage.error('获取资讯失败')
  } finally {
    loading.value = false
  }
}

const auditForm = reactive({
  comment: ''
})

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
    const news = selectedNews.value
    const newStatus = auditAction.value === 'approve' ? 1 : 2
    await axios.post('http://localhost:8080/news/audit', {
      id: Number(news.id),
      status: Number(newStatus),
      auditComment: auditForm.comment
    })
    news.status = newStatus === 1 ? 'approved' : 'rejected'
    news.auditTime = new Date().toLocaleString()
    news.auditComment = auditForm.comment
    ElMessage.success(`审核${auditAction.value === 'approve' ? '通过' : '拒绝'}成功`)
    showAuditDialog.value = false
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

function formatDateTime(val) {
  if (!val) return ''
  // 只保留到秒，去掉毫秒和多余部分
  // 支持 '2025-07-03 07:46:26.000' 或 '2025-07-03T07:46:26.000Z' 等格式
  const match = val.match(/^\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}:\d{2}/)
  return match ? match[0].replace('T', ' ') : val
}

onMounted(() => {
  fetchNewsList()
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

.news-detail h2 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.content {
  line-height: 1.8;
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