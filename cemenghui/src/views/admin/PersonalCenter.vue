<template>
  <div class="personal-center">
    <h2 class="center-title">个人中心</h2>
    <el-divider />
    <el-card>
      <div class="info-section">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :on-change="handleAvatarChange"
          :before-upload="beforeAvatarUpload"
        >
          <template #trigger>
            <div class="avatar-upload-area" :title="user.avatar ? '点击更换头像' : '点击上传头像'">
              <img v-if="user.avatar" :src="user.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon><UserFilled /></el-icon>
                <span>点击上传头像</span>
              </div>
            </div>
          </template>
        </el-upload>
        <div class="user-info">
          <p><strong>用户名：</strong>{{ user.username }}</p>
          <p><strong>邮箱：</strong>{{ user.email }}</p>
          <p><strong>角色：</strong>{{ user.role }}</p>
        </div>
      </div>
      <el-divider />
      <el-form :model="user" label-width="80px" class="edit-form">
        <el-form-item label="手机号">
          <el-input v-model="user.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveInfo">保存资料</el-button>
        </el-form-item>
      </el-form>
      <el-divider />
      <el-form :model="passwordForm" label-width="80px" class="edit-form">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const user = ref({
  avatar: '',
  username: 'admin',
  email: 'admin@example.com',
  role: '超级管理员',
  phone: '13800000000'
})

const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})

function beforeAvatarUpload(file) {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

function handleAvatarChange(file) {
  // 这里只做本地预览，实际项目应上传到服务器
  const reader = new FileReader()
  reader.onload = e => {
    user.value.avatar = e.target.result
    ElMessage.success('头像上传成功（仅本地预览）')
  }
  reader.readAsDataURL(file.raw)
}

function saveInfo() {
  ElMessage.success('资料保存成功（演示）')
}

function changePassword() {
  if (!passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写完整密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  ElMessage.success('密码修改成功（演示）')
  passwordForm.value.newPassword = ''
  passwordForm.value.confirmPassword = ''
}
</script>

<style scoped>
.personal-center {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}
.center-title {
  font-size: 26px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  letter-spacing: 2px;
}
.info-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.avatar-uploader {
  margin-right: 30px;
}
.avatar-upload-area {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border: 2px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbfc;
  cursor: pointer;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(64,158,255,0.08);
  position: relative;
}
.avatar-upload-area:hover {
  border-color: #409eff;
  box-shadow: 0 4px 16px rgba(64,158,255,0.15);
}
.avatar {
  width: 86px;
  height: 86px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #409eff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.15);
}
.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #bfbfbf;
  font-size: 14px;
}
.avatar-placeholder .el-icon {
  font-size: 32px;
  margin-bottom: 4px;
}
.user-info p {
  margin: 0 0 8px 0;
}
.edit-form {
  max-width: 400px;
}
</style> 