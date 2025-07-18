<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 修改密码</h2>
    <el-divider />
    <div class="change-password">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="form.oldPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="form.newPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="form.confirmPassword" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const formRef = ref()

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = reactive({
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const submit = async () => {
  try {
    await formRef.value.validate()
    
    // 从localStorage获取当前用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) {
      ElMessage.error('用户信息不存在，请重新登录')
      return
    }
    
    const userInfo = JSON.parse(userInfoStr)
    
    // 调用后端API修改密码
    const response = await axios.post('http://localhost:8080/updatePassword', {
      username: userInfo.username,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    
    if (response.data && response.data.code === 200) {
      ElMessage.success('密码修改成功！')
      // 清空表单
      resetForm()
    } else {
      ElMessage.error(response.data?.message || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '密码修改失败')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
}
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
.el-form-item {
  margin-bottom: 24px;
}
.el-input {
  border-radius: 8px;
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
</style>