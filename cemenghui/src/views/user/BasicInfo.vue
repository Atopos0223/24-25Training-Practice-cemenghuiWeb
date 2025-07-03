<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 基础信息</h2>
    <el-divider />
    <div class="basic-info">
      <el-form :model="form" label-width="120px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="公司">
          <el-input v-model="form.company"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const form = reactive({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  email: '',
  company: '',
  gender: '男'
})

onMounted(() => {
  // 从localStorage获取用户信息
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    Object.assign(form, userInfo)
  }
})

const submit = async () => {
  try {
    // 准备发送给后端的数据
    const updateData = {
      id: form.id,
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      company: form.company,
      // 将性别转换为数字格式
      gender: form.gender === '男' ? 1 : form.gender === '女' ? 2 : 1
    }
    
    // 调用后端API更新用户信息
    const response = await axios.post('http://localhost:8080/updateUser', updateData)
    
    if (response.data && response.data.code === 200) {
      // 更新localStorage中的用户信息
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr)
        const updatedUserInfo = { 
          ...userInfo, 
          ...form,
          // 将性别转换为数字格式
          gender: form.gender === '男' ? 1 : form.gender === '女' ? 2 : 1
        }
        localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
      }
      
      ElMessage.success('基本信息保存成功！')
    } else {
      ElMessage.error(response.data?.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
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