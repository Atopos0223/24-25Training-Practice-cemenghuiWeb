<template>
  <div class="publish-dynamic">
    <el-form :model="form" label-width="120px">
      <el-form-item label="标题">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="新闻图片">
        <el-upload
          class="upload-demo"
          action="#"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :file-list="fileList"
        >
          <el-button size="small" type="primary">选取文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="内容">
        <el-input type="textarea" v-model="form.content"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.intro"></el-input>
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="form.author"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import type { UploadFile, UploadFiles } from 'element-plus'

const router = useRouter()
const fileList = ref<UploadFile[]>([])
const form = reactive({
  title: '',
  content: '',
  intro: '',
  author: '',
  image: ''
})

const handlePreview = (file: UploadFile) => {
  console.log('预览文件:', file)
}

const handleRemove = (file: UploadFile, files: UploadFiles) => {
  console.log('移除文件:', file, files)
}

const submit = () => {
  const newDynamic = {
    id: Date.now(),
    ...form,
    createTime: new Date().toISOString().split('T')[0],
    status: '审核中'
  }
  console.log('发布动态:', newDynamic)
  router.push('/industry-dynamic/list')
}
</script>

<style scoped>
.publish-dynamic {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>