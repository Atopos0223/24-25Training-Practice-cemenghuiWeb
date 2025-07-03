<template>
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
        <el-input v-model="form.cover" placeholder="请输入会议封面URL"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
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
  content: '',
  cover: ''
})

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入会议名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  content: [{ required: true, message: '请输入会议内容', trigger: 'blur' }]
})

const currentUserId = Number(localStorage.getItem('userId'))
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
const creatorName = userInfo.username || '';

const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    // 调用后端创建会议接口
    const res = await request.post('/api/meeting/create', {
      title: form.name,
      start_time: form.startTime,
      end_time: form.endTime,
      location: form.location,
      content: form.content,
      cover: form.cover,
      creator_id: currentUserId,
      creator_name: creatorName,
      create_time: new Date(),
      status: 1
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
}
</script>

<style scoped>
.create-meeting {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>