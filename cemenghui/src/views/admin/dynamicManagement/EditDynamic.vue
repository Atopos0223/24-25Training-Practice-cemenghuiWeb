<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><TrendCharts /></el-icon> 编辑动态</h2>
    <el-divider />
    <div class="edit-dynamic">
      <el-form :model="form" ref="formRef" label-width="120px">
        <el-form-item label="标题" prop="title" required>
          <el-input v-model="form.title" placeholder="请输入新闻标题" />
        </el-form-item>
        <el-form-item label="新闻图片">
          <el-upload
            action="http://localhost:8080/news/upload"
            name="file"
            :on-success="handleUploadSuccess"
            :show-file-list="false"
          >
            <el-button>选择图片</el-button>
          </el-upload>
          <div v-if="form.image">
            <img :src="form.image" alt="新闻图片" style="max-width: 200px; margin-top: 10px;" />
          </div>
        </el-form-item>
        <el-form-item label="简介" prop="summary">
          <el-input v-model="form.summary" placeholder="请输入新闻简介" />
        </el-form-item>
        <el-form-item label="内容" prop="content" required>
          <el-input type="textarea" v-model="form.content" placeholder="请输入新闻内容" :rows="6" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const form = ref({})
const formRef = ref()

const fetchDetail = async () => {
  try {
    const res = await request.get(`/news/detail/${route.params.id}`)
    if (res.data.code === 200) {
      form.value = res.data.data
    } else {
      ElMessage.error(res.data.msg || '获取详情失败')
    }
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.image = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

const submitForm = async () => {
  try {
    const res = await request.post('/news/update', form.value)
    if (res.data.code === 200) {
      ElMessage.success('保存成功')
      router.back()
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  fetchDetail()
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