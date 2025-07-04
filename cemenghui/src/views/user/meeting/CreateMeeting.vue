<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 创建会议</h2>
    <el-divider />
    <div class="create-meeting">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="会议名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入会议名称"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="会议地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入会议地点"></el-input>
        </el-form-item>
        <el-form-item label="会议内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入会议详细内容"
          ></el-input>
        </el-form-item>
        <el-form-item label="会议封面" prop="cover">
          <el-upload
            :auto-upload="false"
            :file-list="coverFileList"
            :on-remove="handleCoverRemove"
            :on-change="handleCoverChange"
            :limit="1"
            accept="image/*"
          >
            <el-button type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

const formRef = ref<FormInstance>()
const form = reactive({
  name: '',
  startTime: '',
  endTime: '',
  location: '',
  content: ''
})

const coverFile = ref<File | null>(null)
const coverFileList = ref<any[]>([])

const handleCoverChange = (file: any) => {
  coverFile.value = file.raw
  coverFileList.value = [file]
}
const handleCoverRemove = () => {
  coverFile.value = null
  coverFileList.value = []
}

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入会议名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  content: [{ required: true, message: '请输入会议内容', trigger: 'blur' }],
  // cover: [{ required: true, message: '请上传会议封面', trigger: 'change' }]
})

const currentUserId = Number(localStorage.getItem('userId'))
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
const creatorName = userInfo.username || '';

const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (!coverFile.value) {
      ElMessage.error('请上传会议封面')
      return
    }
    const formData = new FormData()
    formData.append('title', form.name)
    formData.append('start_time', form.startTime)
    formData.append('end_time', form.endTime)
    formData.append('location', form.location)
    formData.append('content', form.content)
    formData.append('cover', coverFile.value)
    formData.append('creator_id', currentUserId.toString())
    formData.append('creator_name', creatorName)
    formData.append('create_time', new Date().toISOString())
    formData.append('status', '1')

    const res = await request.post('/api/meeting/create', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.data && res.data.code === 200) {
      ElMessage.success('会议创建成功')
      router.back()
    } else {
      ElMessage.error(res.data?.message || '创建失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('创建失败')
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
  coverFile.value = null
  coverFileList.value = []
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