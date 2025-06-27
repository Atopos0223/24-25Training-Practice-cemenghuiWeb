<template>
  <div class="course-detail">
    <div class="course-info">
      <div class="cover">
        <img :src="course.coverUrl" alt="课程封面" width="200">
      </div>
      <div class="info">
        <p><span>作者:</span> {{ course.author }}</p>
        <p><span>状态:</span> 
          <el-tag :type="getStatusTagType(course.status)">
            {{ course.status }}
          </el-tag>
        </p>
        <p><span>简介:</span> {{ course.intro }}</p>
      </div>
    </div>
    <div class="video">
      <video controls width="100%">
        <source :src="course.videoUrl" type="video/mp4">
      </video>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  course: any
}>()

const getStatusTagType = (status: string) => {
  return status === '已发布' ? 'success' : 'warning'
}
</script>

<style scoped>
.course-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}
.info p {
  margin: 10px 0;
}
.info span {
  display: inline-block;
  width: 60px;
  font-weight: bold;
}
.video {
  margin-top: 20px;
}
</style>