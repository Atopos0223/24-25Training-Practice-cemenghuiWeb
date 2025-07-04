<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Notebook /></el-icon> 编辑课程</h2>
    <el-divider />
    <div class="edit-course">
      <el-button type="primary" @click="handleBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </el-button>

      <el-form :model="form" label-width="120px" v-loading="loading">
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
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Notebook } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const form = ref({
  id: 0,
  title: '',
  author: '',
  status: '已发布',
  coverUrl: '',
  intro: '',
  videoUrl: ''
})
const coverFile = ref(null)
const videoFile = ref(null)
const coverPreview = ref(false)

const getFinalUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http') || url.startsWith('data:')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || window.location.origin
  return `${baseUrl}${url.startsWith('/') ? '' : '/'}${url}`
}


const handleCoverChange = (file) => {
  if (file) {
    coverFile.value = file.raw; // 确保获取的是原始文件
    coverPreview.value = true;
    form.value.coverUrl = URL.createObjectURL(file.raw); // 本地预览
  }
};

const handleVideoChange = (file) => {
  if (file) {
    videoFile.value = file.raw;
  }
};

// 修改loadCourse方法
const loadCourse = async () => {
  try {
    loading.value = true;
    const id = route.params.id;
    console.log('正在加载课程ID:', id);
    
    const res = await axios.get(`/api/course/${id}`);
    console.log('API响应:', res.data);
    
    if (res.data.success) {
      form.value = {
        ...res.data.data,
        id: Number(id),
        // 直接使用后端返回的URL，不再额外处理
        coverUrl: res.data.data.coverUrl || '',
        videoUrl: res.data.data.videoUrl || ''
      };
      console.log('表单数据已设置:', form.value);
    } else {
      throw new Error(res.data.message || '加载课程失败');
    }
  } catch (error) {
    console.error('加载课程错误:', error);
    ElMessage.error(`加载失败: ${error.message}`);
  } finally {
    loading.value = false;
  }
};

const processUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_BASE_URL || window.location.origin}${url}`
}

const submitForm = async () => {
  try {
    loading.value = true;
    const formData = new FormData();
    
    // 添加所有必要字段（确保字段名与后端一致）
    formData.append('id', form.value.id);
    formData.append('title', form.value.title);
    formData.append('author', form.value.author);
    formData.append('status', form.value.status);
    formData.append('intro', form.value.intro || '');

    // 处理文件上传
    if (coverFile.value) {
      formData.append('cover', coverFile.value);
    }
    if (videoFile.value) {
      formData.append('video', videoFile.value);
    }

    // 发送PUT请求（确保路径与后端一致）
    const res = await axios.put(`/api/course/${form.value.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (res.data.success) {
      ElMessage.success('修改成功');
       router.push({ name: 'CoursesManagement' });
    } else {
      throw new Error(res.data.message || '保存失败');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error(error.message || '保存失败');
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  
  router.push('/courses-management');
};

// 确保onMounted调用loadCourse
onMounted(() => {
  console.log('当前课程ID:', route.params.id);
  loadCourse().catch(e => console.error('加载失败:', e));
});
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
  padding: 8px 24px;
  transition: background 0.2s;
}
.el-button:hover {
  background: #53c0ff;
  color: #fff;
}
.back-btn {
  margin-bottom: 20px;
}
.preview-tip {
  margin-left: 10px;
  color: #67c23a;
}
.button-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
}
</style>