<template>
  <div class="info-management-page">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header flex justify-between items-center mb-6">
      <div class="header-title flex items-center">
        <el-icon class="text-blue-500 mr-2"><User /></el-icon>
        <h1 class="text-xl font-semibold text-gray-800">用户信息管理</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="openAddUserModal" class="mr-2">
          <el-icon class="mr-1"><Plus /></el-icon> 新增用户
        </el-button>
        <el-button @click="handleBatchDelete" type="danger" :disabled="selectedUsers.length === 0">
          <el-icon class="mr-1"><Delete /></el-icon> 批量删除
        </el-button>
      </div>
    </div>

    <!-- 搜索过滤区域 -->
    <el-card class="filter-card mb-6">
      <el-form :model="filterForm" :inline="true" label-width="80px">
        <el-form-item label="用户名称">
          <el-input v-model="filterForm.username" placeholder="请输入用户名称" clearable />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="filterForm.phone" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="filterForm.department" placeholder="请输入所属部门" clearable />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色权限">
          <el-select v-model="filterForm.role" placeholder="请选择角色">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 搜索条件显示区域 -->
    <div v-if="getActiveFilters().length > 0" class="active-filters mt-2 p-3 bg-gray-50 rounded-lg border border-gray-200">
      <div class="text-sm text-gray-600">
        <span class="font-medium">当前搜索条件:</span>
        <span v-for="(filter, index) in getActiveFilters()" :key="index" class="ml-2">
          <el-tag type="info" size="small" @close="removeFilter(filter)">
            {{ filter }}
            <template #close>
              <i class="el-icon-close"></i>
            </template>
          </el-tag>
        </span>
        <el-button size="small" type="text" @click="resetFilter" class="ml-2">
          清除全部
        </el-button>
      </div>
    </div>

    <!-- 用户列表区域 -->
    <el-card class="user-list-card">
      <el-table
        v-loading="loading"
        :data="userList"
        stripe
        style="width: 100%"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名称" min-width="120" />
        <el-table-column prop="nickname" label="用户昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号码" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="department" label="所属部门" min-width="150" />
        <el-table-column prop="roleName" label="角色" min-width="100" />
        <el-table-column prop="statusName" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditUserModal(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container flex justify-center mt-4 w-full">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑用户' : '新增用户'"
      width="500px"
      :before-close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名称" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="所属部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="用户状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色权限" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEditing">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" v-if="!isEditing">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete } from '@element-plus/icons-vue';
import { debounce } from 'lodash-es';

// 路由相关
const router = useRouter();
const route = useRoute();

// 数据状态
const userList = ref([] as any[]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const selectedUsers = ref([] as any[]);

// 表单数据
const form = ref({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  department: '',
  status: 1,
  role: 'user',
  password: '',
  confirmPassword: ''
} as any);

// 搜索表单
const filterForm = ref({
  username: '',
  phone: '',
  department: '',
  status: '',
  role: ''
} as any);

// 弹窗状态
const dialogVisible = ref(false);
const isEditing = ref(false);

// 下拉选项
const statusOptions = ref([
  { value: 1, label: '正常' },
  { value: 0, label: '禁用' }
]);

const roleOptions = ref([
  { value: 'superAdmin', label: '超级管理员' },
  { value: 'admin', label: '管理员' },
  { value: 'user', label: '普通用户' }
]);

// 表单验证规则
const rules = ref({
  username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  password: [
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: (rule: any, value: string, callback: any) => {
      if (value !== form.value.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    }, trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色权限', trigger: 'change' }]
});

const formRef = ref(null as any);

// 从本地存储加载用户数据
const loadUsersFromStorage = () => {
  const savedUsers = localStorage.getItem('userList');
  return savedUsers ? JSON.parse(savedUsers) : [];
};

// 保存用户数据到本地存储
const saveUsersToStorage = (users: any[]) => {
  localStorage.setItem('userList', JSON.stringify(users));
};

// 加载用户列表
const fetchUserList = async (page: number = currentPage.value, size: number = pageSize.value) => {
  loading.value = true;
  try {
    // 从本地存储获取用户数据
    const allUsers = loadUsersFromStorage();
    
    // 应用过滤条件
    const filteredData = allUsers.filter(user => (
      (!filterForm.value.username || user.username.includes(filterForm.value.username)) &&
      (!filterForm.value.phone || user.phone.includes(filterForm.value.phone)) &&
      (!filterForm.value.department || user.department.includes(filterForm.value.department)) &&
      (filterForm.value.status === '' || user.status === filterForm.value.status) &&
      (filterForm.value.role === '' || user.role === filterForm.value.role)
    ));
    
    // 分页处理
    const start = (page - 1) * size;
    const end = start + size;
    const paginatedData = filteredData.slice(start, end);
    
    // 更新列表数据
    userList.value = paginatedData.map(item => ({
      ...item,
      roleName: item.role === 'superAdmin' ? '超级管理员' : item.role === 'admin' ? '管理员' : '普通用户',
      statusName: item.status === 1 ? '正常' : '禁用'
    }));
    total.value = filteredData.length;
    
    // 处理空状态
    if (paginatedData.length === 0) {
      if (allUsers.length > 0 && Object.values(filterForm.value).some(value => value !== '')) {
        ElMessage.warning('没有找到匹配的用户');
      } else if (allUsers.length === 0) {
        ElMessage.info('暂无用户数据，请添加新用户');
      }
    }
    
  } catch (error) {
    console.error('获取用户列表失败', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleFilter = () => {
  currentPage.value = 1; // 重置为第一页
  fetchUserList();
  
  // 添加搜索成功提示
  const hasFilters = Object.values(filterForm.value).some(value => value !== '');
  if (hasFilters) {
    ElMessage.success('搜索成功');
  } else {
    ElMessage.info('已显示全部用户');
  }
};

// 重置搜索
const resetFilter = () => {
  filterForm.value = {
    username: '',
    phone: '',
    department: '',
    status: '',
    role: ''
  };
  currentPage.value = 1; // 重置为第一页
  fetchUserList();
  
  // 添加重置提示
  ElMessage.info('已重置搜索条件');
};

// 增强搜索功能 - 防抖处理
const handleLiveSearch = debounce(() => {
  // 只在至少有一个搜索条件时执行实时搜索
  const hasFilters = Object.values(filterForm.value).some(value => value !== '');
  if (hasFilters) {
    currentPage.value = 1; // 重置为第一页
    fetchUserList();
  }
}, 500); // 防抖延迟500ms

// 监听搜索表单变化，实现实时搜索
watch(filterForm, () => {
  handleLiveSearch();
}, { deep: true });

// 获取活跃的搜索条件
const getActiveFilters = () => {
  const activeFilters: string[] = [];
  
  if (filterForm.value.username) {
    activeFilters.push(`用户名称: ${filterForm.value.username}`);
  }
  if (filterForm.value.phone) {
    activeFilters.push(`手机号码: ${filterForm.value.phone}`);
  }
  if (filterForm.value.department) {
    activeFilters.push(`所属部门: ${filterForm.value.department}`);
  }
  if (filterForm.value.status !== '') {
    activeFilters.push(`用户状态: ${filterForm.value.status === 1 ? '正常' : '禁用'}`);
  }
  if (filterForm.value.role) {
    activeFilters.push(`角色权限: ${filterForm.value.role === 'superAdmin' ? '超级管理员' : filterForm.value.role === 'admin' ? '管理员' : '普通用户'}`);
  }
  
  return activeFilters;
};

// 移除单个搜索条件
const removeFilter = (filterText: string) => {
  if (filterText.includes('用户名称')) {
    filterForm.value.username = '';
  } else if (filterText.includes('手机号码')) {
    filterForm.value.phone = '';
  } else if (filterText.includes('所属部门')) {
    filterForm.value.department = '';
  } else if (filterText.includes('用户状态')) {
    filterForm.value.status = '';
  } else if (filterText.includes('角色权限')) {
    filterForm.value.role = '';
  }
  
  // 刷新列表
  currentPage.value = 1;
  fetchUserList();
};

// 打开新增用户弹窗
const openAddUserModal = () => {
  isEditing.value = false;
  form.value = {
    id: null,
    username: '',
    nickname: '',
    phone: '',
    email: '',
    department: '',
    status: 1,
    role: 'user',
    password: '',
    confirmPassword: ''
  };
  nextTick(() => {
    formRef.value?.resetFields();
  });
  dialogVisible.value = true;
};

// 打开编辑用户弹窗
const openEditUserModal = (row: any) => {
  isEditing.value = true;
  form.value = {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    phone: row.phone,
    email: row.email,
    department: row.department,
    status: row.status,
    role: row.role,
    password: '',
    confirmPassword: ''
  };
  dialogVisible.value = true;
};

// 提交表单
const submitForm = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 模拟API请求
        await new Promise((resolve) => {
          setTimeout(() => {
            resolve({
              code: 200,
              message: isEditing.value ? '更新用户成功' : '创建用户成功'
            });
          }, 500);
        });

        // 获取当前用户数据
        const allUsers = loadUsersFromStorage();
        
        if (isEditing.value) {
          // 更新用户
          const index = allUsers.findIndex(user => user.id === form.value.id);
          if (index !== -1) {
            allUsers[index] = {
              ...allUsers[index],
              ...form.value,
              roleName: form.value.role === 'superAdmin' ? '超级管理员' : form.value.role === 'admin' ? '管理员' : '普通用户',
              statusName: form.value.status === 1 ? '正常' : '禁用'
            };
          }
        } else {
          // 创建新用户
          const newId = allUsers.length > 0 ? Math.max(...allUsers.map(user => user.id)) + 1 : 1;
          allUsers.push({
            ...form.value,
            id: newId,
            createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
            roleName: form.value.role === 'superAdmin' ? '超级管理员' : form.value.role === 'admin' ? '管理员' : '普通用户',
            statusName: form.value.status === 1 ? '正常' : '禁用'
          });
        }
        
        // 保存到本地存储
        saveUsersToStorage(allUsers);
        
        // 刷新列表
        ElMessage.success(isEditing.value ? '更新用户成功' : '创建用户成功');
        dialogVisible.value = false;
        fetchUserList(currentPage.value);
        
      } catch (error) {
        console.error('操作失败', error);
        ElMessage.error('操作失败');
      }
    }
  });
};

// 处理删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.nickname} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 模拟API请求
      await new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            message: '删除用户成功'
          });
        }, 500);
      });
      
      // 获取当前用户数据
      const allUsers = loadUsersFromStorage();
      
      // 删除用户
      const updatedUsers = allUsers.filter(user => user.id !== row.id);
      
      // 保存到本地存储
      saveUsersToStorage(updatedUsers);
      
      // 刷新列表
      ElMessage.success('删除用户成功');
      fetchUserList(currentPage.value);
      
    } catch (error) {
      console.error('删除用户失败', error);
      ElMessage.error('删除用户失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户');
    return;
  }

  const userNames = selectedUsers.value.map(user => user.nickname).join(', ');
  ElMessageBox.confirm(`确定要批量删除 ${userNames} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 模拟API请求
      await new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            message: `成功删除 ${selectedUsers.value.length} 个用户`
          });
        }, 500);
      });
      
      // 获取当前用户数据
      const allUsers = loadUsersFromStorage();
      
      // 批量删除用户
      const userIdsToDelete = selectedUsers.value.map(user => user.id);
      const updatedUsers = allUsers.filter(user => !userIdsToDelete.includes(user.id));
      
      // 保存到本地存储
      saveUsersToStorage(updatedUsers);
      
      // 刷新列表
      ElMessage.success(`成功删除 ${selectedUsers.value.length} 个用户`);
      selectedUsers.value = [];
      fetchUserList(currentPage.value);
      
    } catch (error) {
      console.error('批量删除失败', error);
      ElMessage.error('批量删除失败');
    }
  }).catch(() => {
    ElMessage.info('已取消批量删除');
  });
};

// 关闭弹窗
const handleDialogClose = () => {
  dialogVisible.value = false;
};

// 组件挂载时加载数据
onMounted(() => {
  fetchUserList();
});

// 监听路由变化
watch(() => route.path, () => {
  fetchUserList();
});
</script>

<style scoped>
/* 页面整体样式 */
.info-management-page {
  padding: 20px;
}

/* 搜索条件显示的样式 */
.active-filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.active-filters .el-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}

/* 表格加载状态样式 */
.user-list-card .el-table__empty-block {
  min-height: 300px;
}

.pagination-container /deep/ .el-pagination {
  display: flex !important;
  justify-content: center !important;
}

</style>  