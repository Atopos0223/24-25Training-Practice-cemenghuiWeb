<template>
  <div class="course-detail">
    <h2>课程详情</h2>
    <div class="course-info">
      <div class="cover">
        <img :src="course.coverUrl" alt="课程封面" width="200">
      </div>
      <div class="info">
        <p><span>课程名称:</span> {{ course.name }}</p>
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
      <h3>课程视频</h3>
      <video controls width="100%">
        <source :src="course.videoUrl" type="video/mp4">
        您的浏览器不支持视频播放。
      </video>
    </div>
    
    <!-- 调试信息 -->
    <div v-if="!course.name" style="color: red; margin-top: 20px;">
      <p>调试信息：课程数据未正确加载</p>
      <p>路由参数: {{ route.query.course }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const course = ref<any>({})

onMounted(() => {
  // 从路由查询参数获取课程数据
  if (route.query.course) {
    try {
      course.value = JSON.parse(route.query.course as string)
      console.log('课程数据加载成功:', course.value)
    } catch (e) {
      console.error('解析课程数据失败:', e)
    }
  } else {
    console.log('没有找到课程数据')
  }
})

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