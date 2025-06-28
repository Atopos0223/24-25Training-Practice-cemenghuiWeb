<template>
  <div class="add-course">
 
    <el-form :model="form" label-width="120px">
      <el-form-item label="课程名称">
        <el-input v-model="form.name" />
      </el-form-item>
      
      <el-form-item label="课程封面">
        <el-upload :auto-upload="false" :on-change="handleCoverChange">
          <el-button type="primary">上传封面</el-button>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="课程简介">
        <el-input v-model="form.intro" type="textarea" />
      </el-form-item>
      
      <el-form-item label="课程视频">
        <el-upload :auto-upload="false" :on-change="handleVideoChange">
          <el-button type="primary">上传视频</el-button>
        </el-upload>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  name: '',
  coverUrl: '',
  intro: '',
  videoUrl: ''
})

const handleCoverChange = (file: any) => {
  form.coverUrl = URL.createObjectURL(file.raw)
}

const handleVideoChange = (file: any) => {
  form.videoUrl = URL.createObjectURL(file.raw)
}

const submit = () => {
  // 提交逻辑
  router.push('/course')
}
</script>

<style scoped>
.add-course {
  padding: 20px;
}
</style>