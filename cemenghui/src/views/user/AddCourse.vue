<template>
  <div class="add-course">
    <el-form :model="form" label-width="120px">
      <el-form-item label="课程名称" required>
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="作者" required>
        <el-input v-model="form.author" />
      </el-form-item>
      <el-form-item label="课程封面" required>
        <el-upload
          :auto-upload="false"
          :file-list="coverFileList"
          :on-remove="handleCoverRemove"
          :on-change="handleCoverChange"
          :limit="1"
          accept="image/*">
          <el-button type="primary">上传封面</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="课程简介">
        <el-input v-model="form.intro" type="textarea" rows="4" />
      </el-form-item>
      <el-form-item label="课程视频">
        <el-upload
          :auto-upload="false"
          :file-list="videoFileList"
          :on-remove="handleVideoRemove"
          :on-change="handleVideoChange"
          :limit="1"
          accept="video/*">
          <el-button type="primary">上传视频</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="router.push('/userhome/coursemanage/list')">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = reactive({
  title: '',
  author: '',
  intro: '',
  status: '已发布'
})

const coverFile = ref<File | null>(null)
const videoFile = ref<File | null>(null)
const coverFileList = ref<any[]>([])
const videoFileList = ref<any[]>([])

const handleCoverChange = (file: any) => {
  coverFile.value = file.raw  // 注意：file.raw 才是原始 File 对象
  coverFileList.value = [file]
}
const handleCoverRemove = () => {
  coverFile.value = null
  coverFileList.value = []
}
const handleVideoChange = (file: any) => {
  videoFile.value = file.raw
  videoFileList.value = [file]
  return false
}
const handleVideoRemove = () => {
  videoFile.value = null
  videoFileList.value = []
}

const submit = async () => {
  if (!form.title || !form.author) {
    ElMessage.error('课程名称和作者是必填项')
    return
  }
  if (!coverFile.value) {
    ElMessage.error('请上传课程封面')
    return
  }
  const formData = new FormData()
  formData.append('title', form.title)
  formData.append('author', form.author)
  formData.append('intro', form.intro)
  formData.append('status', form.status)
  formData.append('cover', coverFile.value)
  if (videoFile.value) formData.append('video', videoFile.value)

  try {
    const res = await axios.post('/api/course/add', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.data.success) {
      ElMessage.success('添加课程成功')
      router.push('/userhome/coursemanage/list')
    } else {
      ElMessage.error(res.data.message || '添加课程失败')
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error as Error).message)
  }
}
</script>