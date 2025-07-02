<template>
  <div class="audit-meetings">
    <div class="page-header">
      <h2>审核会议</h2>
      <div class="filter-section">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索会议名称或组织者"
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
      <el-table :data="meetingList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="会议名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="organizer" label="组织者" width="120" />
        <el-table-column prop="meetingTime" label="会议时间" width="180" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="auditTime" label="审核时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              size="small" 
              type="success" 
              @click="approveMeeting(scope.row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 'pending'"
              size="small" 
              type="danger" 
              @click="rejectMeeting(scope.row)"
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
    <el-dialog v-model="showDetailDialog" title="会议详情" width="800px">
      <div v-if="selectedMeeting" class="meeting-detail">
        <div class="detail-header">
          <h3>{{ selectedMeeting.name }}</h3>
          <div class="meta-info">
            <span>组织者：{{ selectedMeeting.organizer }}</span>
            <span>会议时间：{{ selectedMeeting.meetingTime }}</span>
            <span>地点：{{ selectedMeeting.location }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>会议简介</h4>
          <p>{{ selectedMeeting.description }}</p>
        </div>
        <div class="detail-section">
          <h4>会议议程</h4>
          <div class="agenda-list">
            <div v-for="(item, index) in selectedMeeting.agenda" :key="index" class="agenda-item">
              <span class="agenda-time">{{ item.time }}</span>
              <span class="agenda-content">{{ item.content }}</span>
            </div>
          </div>
        </div>
        <div v-if="selectedMeeting.auditComment" class="audit-comment">
          <h4>审核意见：</h4>
          <p>{{ selectedMeeting.auditComment }}</p>
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
import request from '@/utils/request'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showAuditDialog = ref(false)
const selectedMeeting = ref(null)
const auditAction = ref('')
const auditFormRef = ref()
const meetingList = ref([])

// 审核表单
const auditForm = reactive({
  comment: ''
})

// 计算属性
const filteredMeetings = computed(() => {
  let filtered = meetingList.value

  if (statusFilter.value) {
    filtered = filtered.filter(meeting => meeting.status === statusFilter.value)
  }

  if (searchKeyword.value) {
    filtered = filtered.filter(meeting => 
      meeting.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      meeting.organizer.toLowerCase().includes(searchKeyword.value.toLowerCase())
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

const viewDetail = (meeting) => {
  selectedMeeting.value = meeting
  showDetailDialog.value = true
}

const approveMeeting = (meeting) => {
  auditAction.value = 'approve'
  selectedMeeting.value = meeting
  auditForm.comment = ''
  showAuditDialog.value = true
}

const rejectMeeting = (meeting) => {
  auditAction.value = 'reject'
  selectedMeeting.value = meeting
  auditForm.comment = ''
  showAuditDialog.value = true
}

const submitAudit = async () => {
  if (auditAction.value === 'reject' && !auditForm.comment.trim()) {
    ElMessage.warning('拒绝审核时必须填写拒绝原因')
    return
  }

  try {
    // 更新会议状态
    const meeting = selectedMeeting.value
    meeting.status = auditAction.value === 'approve' ? 'approved' : 'rejected'
    meeting.auditTime = new Date().toLocaleString()
    meeting.auditComment = auditForm.comment

    ElMessage.success(`审核${auditAction.value === 'approve' ? '通过' : '拒绝'}成功`)
    showAuditDialog.value = false
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const fetchMeetings = async () => {
  const res = await request.get('/api/meeting/list')
  if (res.data && res.data.code === 200) {
    meetingList.value = res.data.data.map(item => ({
      ...item,
      name: item.title
    }))
  }
}

// 生命周期
onMounted(() => {
  fetchMeetings()
  total.value = meetingList.value.length
})
</script>

<style scoped>
.audit-meetings {
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

.meeting-detail {
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

.agenda-list {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.agenda-item {
  display: flex;
  padding: 12px 15px;
  border-bottom: 1px solid #e4e7ed;
}

.agenda-item:last-child {
  border-bottom: none;
}

.agenda-time {
  width: 120px;
  color: #409eff;
  font-weight: bold;
}

.agenda-content {
  flex: 1;
  color: #303133;
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