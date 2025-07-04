<template>
  <div class="edit-course-container">
    <el-button type="primary" @click="handleBack" class="back-btn">
      <el-icon><ArrowLeft /></el-icon>
      返回列表
    </el-button>

    <el-form :model="form" label-width="100px" v-loading="loading">
      <el-form-item label="课程标题" required>
        <el-input v-model="form.title" />
      </el-form-item>

      <el-form-item label="作者" required>
        <el-input v-model="form.author" />
      </el-form-item>

      <el-form-item label="状态">
        <el-select v-model="form.status">
          <el-option label="已发布" value="已发布" />
          <el-option label="审核中" value="审核中" />
          <el-option label="未通过" value="未通过" />
        </el-select>
      </el-form-item>

      <el-form-item label="课程封面">
        <el-upload
          action="#"
          :auto-upload="false"
          :on-change="handleCoverChange"
          :show-file-list="false"
        >
          <el-button type="primary">选择封面</el-button>
          <span v-if="coverPreview" class="preview-tip">已选择新封面</span>
        </el-upload>
        <el-image
          v-if="form.coverUrl && !coverPreview"
          :src="getFinalUrl(form.coverUrl)"
          style="width: 200px; margin-top: 10px"
          fit="cover"
        />
      </el-form-item>

      <el-form-item label="课程视频">
        <el-upload
          action="#"
          :auto-upload="false"
          :on-change="handleVideoChange"
          :show-file-list="false"
        >
          <el-button type="primary">选择视频</el-button>
          <span v-if="videoFile" class="preview-tip">已选择新视频</span>
        </el-upload>
        <video
          v-if="form.videoUrl && !videoFile"
          controls
          style="width: 300px; margin-top: 10px"
          :src="getFinalUrl(form.videoUrl)"
        />
      </el-form-item>

      <el-form-item label="课程简介">
        <el-input v-model="form.intro" type="textarea" rows="4" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm">保存修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

interface FormData {
  id: number
  title: string
  author: string
  status: string
  coverUrl?: string
  intro?: string
  videoUrl?: string
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const form = ref<FormData>({
  id: 0,
  title: '',
  author: '',
  status: '已发布'
})
const coverFile = ref<File | null>(null)
const videoFile = ref<File | null>(null)
const coverPreview = ref(false)

const getFinalUrl = (url?: string): string | undefined => {
  if (!url) return undefined
  if (url.startsWith('http') || url.startsWith('data:')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || window.location.origin
  return `${baseUrl}${url.startsWith('/') ? '' : '/'}${url}`
}

const handleBack = () => {
  router.push('/userhome/coursemanage/list')
}

const handleCoverChange = (file: any) => {
  coverFile.value = (file as any).raw
  coverPreview.value = true
}

const handleVideoChange = (file: any) => {
  videoFile.value = file.raw
}

const loadCourse = async () => {
  try {
    loading.value = true
    const id = route.params.id
    const res = await axios.get(`/api/course/${id}`)
    
    if (res.data.success) {
      form.value = {
        ...res.data.data,
        id: Number(id)
      }
    }
  } catch (error) {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  try {
    loading.value = true
    const formData = new FormData()
    
    // 添加基本字段
    formData.append('title', form.value.title)
    formData.append('author', form.value.author)
    formData.append('status', form.value.status)
    
    // 可选字段
    if (form.value.intro) formData.append('intro', form.value.intro)
    
    // 文件处理 - 使用后端期望的参数名
    if (coverFile.value) formData.append('cover', coverFile.value)
    if (videoFile.value) formData.append('video', videoFile.value)

    console.log('FormData内容:', {
      title: form.value.title,
      author: form.value.author,
      status: form.value.status,
      intro: form.value.intro,
      hasCover: !!coverFile.value,
      hasVideo: !!videoFile.value
    })

    // 修改API端点以匹配后端
    const res = await axios.put(`/api/course/${form.value.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.data.success) {
      ElMessage.success('修改成功')
      coverFile.value = null
      videoFile.value = null
      coverPreview.value = false
      router.push('/userhome/coursemanage/list')
    } else {
      throw new Error(res.data.message || '修改失败')
    }
  } catch (error) {
    console.error('提交错误详情:', error)
    ElMessage.error(error instanceof Error ? error.message : '修改失败')
  } finally {
    loading.value = false
  }
}

// 确保onMounted调用loadCourse
onMounted(() => {
  console.log('当前课程ID:', route.params.id);
  loadCourse().catch(e => console.error('加载失败:', e));
});
</script>

<style scoped>
.edit-course-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.back-btn {
  margin-bottom: 20px;
}
.preview-tip {
  margin-left: 10px;
  color: #67c23a;
}
</style>