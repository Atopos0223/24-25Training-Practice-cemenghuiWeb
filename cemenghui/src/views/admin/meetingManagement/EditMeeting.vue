<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 编辑会议</h2>
    <el-divider />
    <div class="edit-meeting">
      <el-form :model="form" label-width="120px" ref="formRef">
        <el-form-item label="会议名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入会议名称"></el-input>
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
          <el-button type="primary" @click="submitForm">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const form = reactive({
  id: '',
  title: '',
  startTime: '',
  endTime: '',
  location: '',
  content: '',
  cover: '',
  status: ''
})

onMounted(async () => {
  const id = route.query.id
  if (id) {
    const res = await request.get(`/api/meeting/detail/${id}`)
    if (res.data && res.data.code === 200) {
      Object.assign(form, res.data.data)
    }
  }
})

const submitForm = async () => {
  if (form.status === 3 || form.status === '3') {
    form.status = 1;
  }
  const res = await request.put('/api/meeting/update', form)
  if (res.data && res.data.code === 200) {
    ElMessage.success('修改成功')
    router.back()
  } else {
    ElMessage.error(res.data?.message || '修改失败')
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