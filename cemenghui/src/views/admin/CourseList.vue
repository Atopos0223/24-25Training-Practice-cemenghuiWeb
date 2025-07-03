<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Notebook /></el-icon> 课程列表</h2>
    <el-divider />
    <div class="course-list">
      <div class="action-bar">
        <el-input v-model="searchKey" placeholder="搜索课程" style="width: 300px" />
        <el-button type="primary" @click="goToAdd">添加课程</el-button>
        <el-button @click="goToAudit">审核管理</el-button>
      </div>

      <el-table :data="filteredCourses" border>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="status" label="状态">
          <template #default="{row}">
            <el-tag :type="getStatusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="viewDetail(row)">查看</el-button>
            <el-button type="warning" size="small" @click="editCourse(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteCourse(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 详情弹窗 -->
      <el-dialog v-model="detailVisible" title="课程详情" width="70%">
        <CourseDetail v-if="currentCourse" :course="currentCourse" />
      </el-dialog>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'


const router = useRouter()
const searchKey = ref('')
const detailVisible = ref(false)
const currentCourse = ref<any>(null)

const courses = ref([
  {
    id: 1,
    name: 'Web前端开发实战',
    author: '王教授',
    status: '已发布',
    coverUrl: 'https://picsum.photos/200/150',
    intro: '本课程全面讲解Web前端开发技术栈...',
    videoUrl: 'https://example.com/course1.mp4'
  }
])

const filteredCourses = computed(() => {
  return courses.value.filter(c => 
    c.name.includes(searchKey.value) || 
    c.author.includes(searchKey.value)
  )
})

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    '已发布': 'success',
    '审核中': 'warning',
    '未通过': 'danger'
  }
  return map[status] || ''
}

const viewDetail = (course: any) => {
  currentCourse.value = course
  detailVisible.value = true
}

const editCourse = (course: any) => {
  router.push(`/course-manage/edit/${course.id}`)
}

const deleteCourse = (id: number) => {
  courses.value = courses.value.filter(c => c.id !== id)
}

const goToAdd = () => {
  router.push('/course-manage/add')
}

const goToAudit = () => {
  router.push('/course-manage/audit')
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
.el-table {
  border-radius: 12px;
  overflow: hidden;
}
.el-table--striped .el-table__body tr.el-table__row--striped {
  background: #f6faff;
}
.el-table__body tr:hover > td {
  background: #e6f7ff !important;
}
.course-list {
  padding: 20px;
}
.action-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
</style>