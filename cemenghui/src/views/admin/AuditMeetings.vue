<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 审核会议</h2>
    <el-divider />
    <div class="audit-meetings">
      <div class="page-header">
        <h2>审核会议</h2>
        <div class="filter-section">
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px">
            <el-option label="全部" value="" />
            <el-option label="未审核" value="1" />
            <el-option label="已通过" value="2" />
            <el-option label="被驳回" value="3" />
            <el-option label="草稿" value="0" />
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

      <el-table :data="pagedMeetings" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="序号" width="80" />
        <el-table-column prop="name" label="会议名称" min-width="120" />
        <el-table-column prop="creator_name" label="组织者" width="100" />
        <el-table-column prop="startTime" label="会议时间" width="160" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            {{ statusText(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <div class="button-row">
              <el-button size="small" type="primary" @click="viewDetail(row.id)">查看</el-button>
              <el-button size="small" type="success" @click="auditMeeting(row.id, 2)" v-if="row.status == 1">通过</el-button>
              <el-button size="small" type="danger" @click="auditMeeting(row.id, 3)" v-if="row.status == 1">驳回</el-button>
            </div>
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
    </div>

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
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Calendar } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref("")
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showAuditDialog = ref(false)
const selectedMeeting = ref(null)
const auditAction = ref('')
const auditFormRef = ref()
const meetingList = ref([])
const router = useRouter()

// 审核表单
const auditForm = reactive({
  comment: ''
})

// 计算属性
const filteredMeetings = computed(() => {
  let filtered = meetingList.value

  if (statusFilter.value) {
    filtered = filtered.filter(meeting => String(meeting.status) === statusFilter.value)
  }

  if (searchKeyword.value) {
    filtered = filtered.filter(meeting => 
      meeting.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      meeting.creator_name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  return filtered
})

const pagedMeetings = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  total.value = filteredMeetings.value.length
  return filteredMeetings.value.slice(start, end)
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

const statusText = (status) => {
  if (status === 1 || status === '1') return '未审核'
  if (status === 2 || status === '2') return '已通过'
  if (status === 3 || status === '3') return '被驳回'
  if (status === 0 || status === '0') return '草稿'
  return '未知'
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const viewDetail = (id) => {
  router.push(`/adminhome/meetingmanage/detail/${id}`)
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
      name: item.title,
      creator_name: item.creator_name,
      startTime: item.startTime || item.start_time
    }))
  }
}

const auditMeeting = async (id, status) => {
  const res = await request.post('/api/meeting/audit', { id, status })
  if (res.data && res.data.code === 200) {
    ElMessage.success(status === 2 ? '审核通过' : '已驳回')
    fetchMeetings()
  } else {
    ElMessage.error(res.data?.message || '操作失败')
  }
}

// 监听筛选和搜索变化时重置页码
watch([statusFilter, searchKeyword], () => {
  currentPage.value = 1
})

// 生命周期
onMounted(() => {
  fetchMeetings()
  total.value = meetingList.value.length
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
.el-table {
  border-radius: 12px;
  overflow: hidden;
}
.el-table--striped .el-table__body tr.el-table__row--striped {
  background: #f6faff;
}
.el-table__body tr:hover > td {
  background: #e6f7ff !important;
}
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

.button-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
}

.el-table .el-table__cell {
  padding: 12px 16px;
}
</style> 