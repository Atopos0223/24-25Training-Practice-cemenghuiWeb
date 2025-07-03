<template>
  <div class="info-management-page">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header flex justify-between items-center mb-6">
      <div class="header-title flex items-center">
        <h1 class="text-xl font-semibold text-gray-800">用户信息管理</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="openAddUserModal" class="mr-2">
          <el-icon class="mr-1"><Plus /></el-icon> 新增用户
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
        <el-form-item label="公司名称">
          <el-input v-model="filterForm.company" placeholder="请输入公司名称" clearable />
        </el-form-item>
        <el-form-item label="角色权限">
          <el-select v-model="filterForm.is_super" placeholder="请选择角色">
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filterForm.gender" placeholder="请选择性别">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="0" />
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
        <el-table-column prop="company" label="公司名称" min-width="150" />
        <el-table-column prop="is_super" label="角色" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.is_super === 1 ? 'danger' : 'info'">
              {{ row.is_super === 1 ? '超级管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? 'primary' : 'success'">
              {{ row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.create_time) }}
          </template>
        </el-table-column>
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
        <el-form-item label="公司名称" prop="company">
          <el-input v-model="form.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="用户状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色权限" prop="is_super">
          <el-select v-model="form.is_super" placeholder="请选择角色">
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="0" />
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
import request from '@/utils/request';

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
  company: '',
  status: 1,
  is_super: 0,
  gender: 1,
  password: '',
  confirmPassword: ''
} as any);

// 搜索表单
const filterForm = ref({
  username: '',
  phone: '',
  company: '',
  status: '',
  is_super: '',
  gender: ''
} as any);

// 弹窗状态
const dialogVisible = ref(false);
const isEditing = ref(false);

// 下拉选项
const statusOptions = ref([
  { value: 1, label: '正常' },
  { value: 0, label: '禁用' }
]);

const isSuperOptions = ref([
  { value: 1, label: '超级管理员' },
  { value: 0, label: '普通用户' }
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
  is_super: [{ required: true, message: '请选择角色权限', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
});

const formRef = ref(null as any);

// 加载用户列表
const fetchUserList = async (page: number = currentPage.value, size: number = pageSize.value) => {
  loading.value = true;
  try {
    const params: any = {
      username: filterForm.value.username,
      phone: filterForm.value.phone,
      company: filterForm.value.company,
      status: filterForm.value.status,
      is_super: filterForm.value.is_super,
      gender: filterForm.value.gender
    };
    const res = await request.get('http://localhost:8080/userList', {
      params,
      headers: { 'Cache-Control': 'no-cache' }
    });
    if (res.data.code === 200) {
      const allUsers = res.data.data || [];
      userList.value = allUsers.slice().sort((a, b) => a.id - b.id);
      total.value = allUsers.length;
    } else {
      userList.value = [];
      total.value = 0;
      ElMessage.error(res.data.msg || '获取用户列表失败');
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
    company: '',
    status: '',
    is_super: '',
    gender: ''
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
  if (filterForm.value.company) {
    activeFilters.push(`公司名称: ${filterForm.value.company}`);
  }
  if (filterForm.value.status !== '') {
    activeFilters.push(`用户状态: ${filterForm.value.status === 1 ? '正常' : '禁用'}`);
  }
  if (filterForm.value.is_super !== '') {
    activeFilters.push(`角色权限: ${filterForm.value.is_super == 1 ? '超级管理员' : '普通用户'}`);
  }
  if (filterForm.value.gender !== '') {
    activeFilters.push(`性别: ${filterForm.value.gender === 1 ? '男' : '女'}`);
  }
  
  return activeFilters;
};

// 移除单个搜索条件
const removeFilter = (filterText: string) => {
  if (filterText.includes('用户名称')) {
    filterForm.value.username = '';
  } else if (filterText.includes('手机号码')) {
    filterForm.value.phone = '';
  } else if (filterText.includes('公司名称')) {
    filterForm.value.company = '';
  } else if (filterText.includes('用户状态')) {
    filterForm.value.status = '';
  } else if (filterText.includes('角色权限')) {
    filterForm.value.is_super = '';
  } else if (filterText.includes('性别')) {
    filterForm.value.gender = '';
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
    company: '',
    status: 1,
    is_super: 0,
    gender: 1,
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
    company: row.company,
    status: row.status,
    is_super: row.is_super,
    gender: row.gender,
    password: '',
    confirmPassword: ''
  };
  dialogVisible.value = true;
};

// 提交表单
const statusMap = { '正常': 1, '禁用': 0, 1: 1, 0: 0 };
const submitForm = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const payload = {
          ...form.value,
          status: statusMap[form.value.status]
        };
        let res;
        if (isEditing.value) {
          res = await request.post('/updateUser', { ...payload, id: form.value.id });
        } else {
          res = await request.post('/addUser', payload);
        }
        if (res.data.code === 200) {
          if (isEditing.value) {
            ElMessage.success('更新用户成功');
            dialogVisible.value = false;
            fetchUserList(currentPage.value);
          } else {
            ElMessage.success('创建用户成功');
            dialogVisible.value = false;
            currentPage.value = 1;
            fetchUserList(1);
          }
        } else {
          ElMessage.error(res.data.msg || '操作失败');
        }
      } catch (error) {
        ElMessage.error('操作失败');
      }
    }
  });
};

// 删除单个用户
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.nickname} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/deleteUser/${row.id}`);
      if (res.data.code === 200) {
        ElMessage.success('删除用户成功');
        fetchUserList(currentPage.value);
      } else {
        ElMessage.error(res.data.msg || '删除用户失败');
      }
    } catch (error) {
      console.error('删除用户失败', error);
      ElMessage.error('删除用户失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
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

function formatDateTime(val) {
  if (!val) return '';
  return val.replace('T', ' ').slice(0, 19);
}
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