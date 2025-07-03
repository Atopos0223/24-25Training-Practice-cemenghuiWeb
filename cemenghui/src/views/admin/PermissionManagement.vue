<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 权限管理</h2>
    <el-divider />
    <div class="permission-management">
      <!-- 用户权限列表 -->
      <el-card class="user-list-card">
        <template #header>
          <div class="card-header">
            <span>用户权限列表</span>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名或邮箱"
              style="width: 300px"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </template>

        <el-table :data="filteredUsers" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="email" label="邮箱" width="200" />
          <el-table-column prop="is_super" label="角色" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.is_super === 1 ? 'danger' : 'info'">
                {{ getRoleName(scope.row.is_super) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="create_time" label="创建时间" min-width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.create_time) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button 
                size="small" 
                type="danger" 
                @click="toggleUserStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
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

      <!-- 添加/编辑用户对话框 -->
      <el-dialog
        v-model="showAddUserDialog"
        :title="editingUser ? '编辑用户' : '添加用户'"
        width="500px"
      >
        <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
              <el-option label="普通用户" value="user" />
              <el-option label="管理员" value="admin" />
              <el-option label="超级管理员" value="super_admin" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="userForm.status">
              <el-radio label="active">启用</el-radio>
              <el-radio label="inactive">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showAddUserDialog = false">取消</el-button>
            <el-button type="primary" @click="saveUser">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, User } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddUserDialog = ref(false)
const editingUser = ref(null)
const userFormRef = ref()
const users = ref([])

// 表单数据
const userForm = reactive({
  username: '',
  email: '',
  role: '',
  status: 'active'
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 计算属性
const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }
  return users.value.filter(user => 
    user.username.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    user.email.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 方法
const getRoleName = (is_super) => {
  return is_super === 1 ? '超级管理员' : '普通用户'
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const editUser = (user) => {
  editingUser.value = user
  Object.assign(userForm, user)
  showAddUserDialog.value = true
}

const toggleUserStatus = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.status === 1 ? '禁用' : '启用'}用户 "${user.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    // 调用后端接口更新用户状态，只传id和status
    const res = await request.post('http://localhost:8080/updateUserStatus', {
      id: Number(user.id),
      status: user.status === 1 ? 0 : 1
    });
    if (res.data.code === 200) {
      ElMessage.success(`用户状态${user.status === 1 ? '禁用' : '启用'}成功`);
      fetchUserList(); // 刷新列表
    } else {
      ElMessage.error(res.data.msg || '操作失败');
    }
  } catch (error) {
    // 用户取消操作或请求失败
  }
};

const saveUser = async () => {
  try {
    await userFormRef.value.validate()
    
    if (editingUser.value) {
      // 编辑用户
      const index = users.value.findIndex(u => u.id === editingUser.value.id)
      if (index !== -1) {
        Object.assign(users.value[index], userForm)
      }
      ElMessage.success('用户信息更新成功')
    } else {
      // 添加用户
      const newUser = {
        id: users.value.length + 1,
        ...userForm,
        create_time: new Date().toISOString()
      }
      users.value.push(newUser)
      ElMessage.success('用户添加成功')
    }
    
    showAddUserDialog.value = false
    resetForm()
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const resetForm = () => {
  editingUser.value = null
  Object.assign(userForm, {
    username: '',
    email: '',
    role: '',
    status: 'active'
  })
  userFormRef.value?.resetFields()
}

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await request.get('/userList', {
      headers: { 'Cache-Control': 'no-cache' }
    })
    if (res.data.code === 200) {
      users.value = (res.data.data || []).slice().sort((a, b) => a.id - b.id)
    } else {
      users.value = []
      ElMessage.error(res.data.msg || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function formatDateTime(val) {
  if (!val) return '';
  return val.replace('T', ' ').slice(0, 19);
}

// 生命周期
onMounted(() => {
  fetchUserList()
  total.value = users.value.length
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
.permission-management {
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

.user-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 