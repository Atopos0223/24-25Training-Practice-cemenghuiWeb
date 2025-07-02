<template>
  <div class="publish-news">
    <el-form :model="form" label-width="120px" ref="formRef">
      <el-form-item label="标题" :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]">
        <el-input v-model="form.title" placeholder="请输入新闻标题"></el-input>
      </el-form-item>
      
      <el-form-item label="新闻图片">
        <el-upload
          class="upload-demo"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :file-list="fileList"
          :limit="1"
          accept="image/*"
        >
          <el-button size="small" type="primary">选择图片</el-button>
          <template #tip>
            <div class="el-upload__tip">只能上传一张图片，格式为jpg/png</div>
          </template>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="简介" :rules="[{ max: 200, message: '简介不能超过200个字符', trigger: 'blur' }]">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入新闻简介"></el-input>
      </el-form-item>
      
      <el-form-item label="内容" :rules="[{ required: true, message: '请输入新闻内容', trigger: 'blur' }]">
        <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入新闻内容"></el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import type { UploadFile, FormInstance } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const fileList = ref<UploadFile[]>([])
const selectedFile = ref<File | null>(null)
const userInfo = ref<any>({})

const form = reactive({
  title: '',
  content: '',
  summary: '',
  author_id: 0, // 初始为0，onMounted和submit时赋值
  image: '',
  status: 1,
  create_time: new Date().toISOString().split('T')[0],
  update_time: new Date().toISOString().split('T')[0]
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
    form.author_id = userInfo.value.id || 0
  }
})

// 处理文件选择
const handleFileChange = (file: UploadFile) => {
  selectedFile.value = file.raw as File
}

// 图片预览
const handlePreview = (file: UploadFile) => {
  console.log('预览图片:', file)
  // 实际项目中可使用图片预览组件
}

// 移除图片
const handleRemove = (file: UploadFile) => {
  console.log('移除图片:', file)
  selectedFile.value = null
  form.image = ''
}

// 表单提交
const submit = async () => {
  // 提交前再赋值一次，确保是最新的
  const info = localStorage.getItem('userInfo')
  form.author_id = info ? JSON.parse(info).id : 0
  
  formRef.value?.validate(async (valid) => {
    if (!valid) return false
    
    try {
      
      if (selectedFile.value) {
        const formData = new FormData()
        formData.append('file', selectedFile.value)
        
        
        const newsResponse = await axios.post('http://localhost:8080/addNews', form)
        
        if (newsResponse.data.code === 200) {
          ElMessage.success('发布成功')
          
        } else {
          ElMessage.error(newsResponse.data.message || '发布失败')
        }
      }
    } catch (error: any) {
      console.error('提交失败:', error)
      ElMessage.error('系统错误，请稍后重试')
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  fileList.value = []
  selectedFile.value = null
  form.image = ''
}
</script>

<style scoped>
.publish-news {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>
    