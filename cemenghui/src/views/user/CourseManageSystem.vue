<template>
  <div class="course-manage-system">
    <h2>Web端课程管理子系统</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="添加课程" name="add">
        <el-form :model="courseForm" label-width="120px">
          <el-form-item label="课程名称">
            <el-input v-model="courseForm.name"></el-input>
          </el-form-item>
          <el-form-item label="课程封面">
            <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :on-change="handleCoverChange"
              :on-remove="handleRemove"
              :file-list="coverFileList"
            >
              <el-button type="primary">选取文件</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="课程简介">
            <el-input type="textarea" v-model="courseForm.intro"></el-input>
          </el-form-item>
          <el-form-item label="课程排序">
            <el-input-number v-model="courseForm.sort" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="课程视频">
            <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :on-change="handleVideoChange"
              :file-list="videoFileList"
            >
              <el-button type="primary">选取视频</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="courseForm.author"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addCourse">添加</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="课程列表" name="list">
        <el-input 
          v-model="searchKey" 
          placeholder="请输入课程名称搜索" 
          style="width: 300px; margin-bottom: 15px;"
        />
        <el-table :data="filteredCourseList" border style="width: 100%">
          <el-table-column prop="name" label="课程名称"></el-table-column>
          <el-table-column prop="author" label="作者"></el-table-column>
          <el-table-column prop="sort" label="排序" width="80"></el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{row}">
              <el-tag :type="getStatusTagType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewCourse(scope.row)">查看</el-button>
              <el-button type="warning" size="small" @click="editCourse(scope.row)" 
                v-if="scope.row.status !== '已发布'">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteCourse(scope.row)" 
                v-if="scope.row.status !== '已发布'">删除</el-button>
              <el-button type="success" size="small" @click="submitAudit(scope.row)" 
                v-if="scope.row.status === '未审核'">提交审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="课程详情" name="detail" v-if="activeCourse">
        <el-card>
          <h3>{{ activeCourse.name }}</h3>
          <div class="course-info">
            <div class="cover">
              <img :src="activeCourse.coverUrl" alt="课程封面" width="200">
            </div>
            <div class="info">
              <p><span>作者:</span> {{ activeCourse.author }}</p>
              <p><span>排序:</span> {{ activeCourse.sort }}</p>
              <p><span>状态:</span> 
                <el-tag :type="getStatusTagType(activeCourse.status)">
                  {{ activeCourse.status }}
                </el-tag>
              </p>
              <p><span>简介:</span> {{ activeCourse.intro }}</p>
            </div>
          </div>
          <div class="video">
            <video width="640" height="360" controls>
              <source :src="activeCourse.videoUrl" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
          </div>
        </el-card>
      </el-tab-pane>
      
 
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import type { UploadFile, UploadFiles } from 'element-plus';

interface Course {
  id: number;
  name: string;
  coverUrl: string;
  intro: string;
  sort: number;
  videoUrl: string;
  author: string;
  status: '未审核' | '审核中' | '已通过' | '已发布' | '未通过';
  submitTime?: string;
  reason?: string;
}

const activeTab = ref<'add' | 'list' | 'detail' | 'audit'>('add');
const searchKey = ref('');
const coverFileList = ref<UploadFile[]>([]);
const videoFileList = ref<UploadFile[]>([]);
const activeCourse = ref<Course | null>(null);

const courseForm = reactive({
  name: '',
  coverUrl: '',
  intro: '',
  sort: 0,
  videoUrl: '',
  author: ''
});

const courseList = ref<Course[]>([
  {
    id: 1,
    name: 'Web前端开发实战',
    coverUrl: 'https://picsum.photos/200/150',
    intro: '本课程全面讲解Web前端开发技术栈...',
    sort: 1,
    author: '王教授',
    status: '已发布',
    videoUrl: 'https://example.com/course1.mp4'
  },
  {
    id: 2,
    name: 'Python数据分析基础',
    coverUrl: 'https://picsum.photos/200/150?random=1',
    intro: '从零开始学习Python数据分析...',
    sort: 2,
    author: '李老师',
    status: '未审核',
    videoUrl: 'https://example.com/course2.mp4'
  }
]);

const auditList = ref<Course[]>([
  {
    id: 2,
    name: 'Python数据分析基础',
    author: '李老师',
    submitTime: '2025-06-20',
    status: '审核中',
    videoUrl: 'https://example.com/course2.mp4',
	coverUrl:'',
	intro:'',
	sort:0
  },
  {
    id: 3,
    name: 'UI设计入门',
    author: '张设计师',
    submitTime: '2025-06-18',
    status: '未通过',
    reason: '视频质量不达标',
    videoUrl: 'https://example.com/course3.mp4',
	coverUrl:'',
	intro:'',
	sort:0
  }
]);

const filteredCourseList = computed(() => {
  return courseList.value.filter(course => 
    course.name.toLowerCase().includes(searchKey.value.toLowerCase())
  );
});

const handleCoverChange = (file: UploadFile, files: UploadFiles) => {
  coverFileList.value = files;
  // 这里可以添加文件预览或上传逻辑
  const reader = new FileReader();
  reader.onload = (e) => {
    courseForm.coverUrl = e.target?.result as string;
  };
  reader.readAsDataURL(file.raw as Blob);
};

const handleVideoChange = (file: UploadFile, files: UploadFiles) => {
  videoFileList.value = files;
  courseForm.videoUrl = URL.createObjectURL(file.raw as Blob);
};

const handleRemove = (_file: UploadFile, files: UploadFiles) => {
  coverFileList.value = files;
  courseForm.coverUrl = '';
};

const getStatusTagType = (status: Course['status']) => {
  switch(status) {
    case '已发布': return 'success';
    case '审核中': return 'warning';
    case '未通过': return 'danger';
    case '已通过': return '';
    default: return 'info';
  }
};

const addCourse = () => {
  const newCourse: Course = {
    id: courseList.value.length + 1,
    ...courseForm,
    status: '未审核'
  };
  courseList.value.push(newCourse);
  resetForm();
  activeTab.value = 'list';
};

const resetForm = () => {
  courseForm.name = '';
  courseForm.coverUrl = '';
  courseForm.intro = '';
  courseForm.sort = 0;
  courseForm.videoUrl = '';
  courseForm.author = '';
  coverFileList.value = [];
  videoFileList.value = [];
};

const viewCourse = (row: Course) => {
  activeCourse.value = row;
  activeTab.value = 'detail';
};

const editCourse = (row: Course) => {
  Object.assign(courseForm, {
    name: row.name,
    coverUrl: row.coverUrl,
    intro: row.intro,
    sort: row.sort,
    videoUrl: row.videoUrl,
    author: row.author
  });
  activeTab.value = 'add';
};

const deleteCourse = (row: Course) => {
  courseList.value = courseList.value.filter(item => item.id !== row.id);
};

const submitAudit = (row: Course) => {
  row.status = '审核中';
  auditList.value.push({
    ...row,
    submitTime: new Date().toISOString().split('T')[0]
  });
};
</script>

<style scoped>
.course-manage-system {
  padding: 20px;
}

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
  color: #606266;
  font-weight: bold;
}

.video {
  margin-top: 20px;
  text-align: center;
}

.upload-demo {
  margin-bottom: 10px;
}
</style>