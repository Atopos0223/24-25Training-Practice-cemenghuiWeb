<template>
  <div class="course-edit">
    <h2>编辑课程</h2>
    <el-form 
      v-if="course" 
      :model="course" 
      label-width="120px"
      label-position="top"
    >
      <el-form-item label="课程名称" required>
        <el-input v-model="course.name" placeholder="请输入课程名称" />
      </el-form-item>
      
      <el-form-item label="作者" required>
        <el-input v-model="course.author" placeholder="请输入作者姓名" />
      </el-form-item>
      
      <el-form-item label="封面图片">
        <el-upload
          action="https://jsonplaceholder.typicode.com/posts/"
          :show-file-list="false"
          :on-success="handleCoverUpload"
          :before-upload="beforeCoverUpload"
        >
          <img v-if="course.coverUrl" :src="course.coverUrl" class="cover-image" />
          <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="课程简介" required>
        <el-input 
          v-model="course.intro" 
          type="textarea" 
          :rows="4" 
          placeholder="请输入课程简介"
        />
      </el-form-item>
      
      <el-form-item label="课程视频">
        <el-input v-model="course.videoUrl" placeholder="请输入视频URL" />
      </el-form-item>
      
      <el-form-item label="课程状态">
        <el-select v-model="course.status" placeholder="请选择状态">
          <el-option label="已发布" value="已发布" />
          <el-option label="审核中" value="审核中" />
          <el-option label="未通过" value="未通过" />
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const course = ref(null)

// Mock data - in a real app, this would come from an API
const mockCourses = [
  {
    id: 1,
    name: 'Web前端开发实战',
    author: '王教授',
    status: '已发布',
    coverUrl: 'https://picsum.photos/200/150',
    intro: '本课程全面讲解Web前端开发技术栈...',
    videoUrl: 'https://example.com/course1.mp4'
  }
]

onMounted(() => {
  const id = parseInt(route.params.id)
  // Find the course in mock data
  const foundCourse = mockCourses.find(c => c.id === id)
  if (foundCourse) {
    course.value = { ...foundCourse }
  } else {
    ElMessage.error('课程不存在')
    router.push('/course-manage/list')
  }
})

function save() {
  // In a real app, this would be an API call
  console.log('Saving course:', course.value)
  ElMessage.success('课程保存成功')
  router.push('/course-manage/list')
}

function cancel() {
  router.push('/course-manage/list')
}

function handleCoverUpload(response, file) {
  // In a real app, this would get the URL from the response
  course.value.coverUrl = URL.createObjectURL(file.raw)
}

function beforeCoverUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
  }

  return isImage && isLt2M
}
</script>

<style scoped>
.course-edit {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.cover-image {
  width: 200px;
  height: 150px;
  object-fit: cover;
  display: block;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 150px;
  line-height: 150px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>