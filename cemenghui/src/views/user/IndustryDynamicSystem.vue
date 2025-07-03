<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><TrendCharts /></el-icon> 行业动态系统</h2>
    <el-divider />
    <div class="industry-dynamic-system">
      <h2>Web端行业动态管理子系统</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="发布动态" name="publish">
          <el-form :model="dynamicForm" label-width="120px">
            <el-form-item label="标题">
              <el-input v-model="dynamicForm.title"></el-input>
            </el-form-item>
            <el-form-item label="新闻图片">
              <el-upload
                class="upload-demo"
                action="#"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :file-list="fileList"
              >
                <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
              </el-upload>
            </el-form-item>
            <el-form-item label="内容">
              <el-input type="textarea" v-model="dynamicForm.content"></el-input>
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="dynamicForm.intro"></el-input>
            </el-form-item>
            <el-form-item label="作者">
              <el-input v-model="dynamicForm.author"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="publishDynamic">发布</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="动态列表" name="list">
          <el-input 
            v-model="searchKey" 
            placeholder="请输入关键字搜索" 
            style="width: 300px; margin-bottom: 15px;"
          />
          <el-table :data="dynamicList" border style="width: 100%">
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="author" label="作者"></el-table-column>
            <el-table-column prop="createTime" label="发布时间"></el-table-column>
            <el-table-column prop="status" label="审核状态"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
                <el-button type="text" @click="editDynamic(scope.row)" 
                  v-if="scope.row.status !== '已发布'">编辑</el-button>
                <el-button type="text" @click="deleteDynamic(scope.row)" 
                  v-if="scope.row.status !== '已发布'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="dynamicList.length"
            style="margin-top: 15px;"
          ></el-pagination>
        </el-tab-pane>
        
        <el-tab-pane label="动态详情" name="detail" v-if="activeDynamic">
          <el-card>
            <h3>{{ activeDynamic.title }}</h3>
            <div class="info">
              <span>作者: {{ activeDynamic.author }}</span>
              <span>发布时间: {{ activeDynamic.createTime }}</span>
              <span>状态: {{ activeDynamic.status }}</span>
            </div>
            <div class="content" v-html="activeDynamic.content"></div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import type { UploadFile, UploadFiles } from 'element-plus';

interface Dynamic {
  id: number;
  title: string;
  content: string;
  intro: string;
  author: string;
  image?: string;
  createTime: string;
  status: '已发布' | '审核中' | '未通过';
}

const activeTab = ref<'publish' | 'list' | 'detail'>('publish');
const searchKey = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const fileList = ref<UploadFile[]>([]);
const activeDynamic = ref<Dynamic | null>(null);

const dynamicForm = reactive({
  title: '',
  content: '',
  intro: '',
  author: '',
  image: ''
});

const dynamicList = ref<Dynamic[]>([
  {
    id: 1,
    title: '行业最新动态：AI技术在教育领域的应用',
    author: '张三',
    content: '<p>人工智能技术正在改变教育行业...</p>',
    intro: '本文介绍了AI技术在教育领域的最新应用案例...',
    createTime: '2025-06-15',
    status: '已发布'
  },
  {
    id: 2,
    title: '职业教育发展趋势分析',
    author: '李四',
    content: '<p>随着经济结构调整，职业教育需求日益增长...</p>',
    intro: '本文分析了未来5年职业教育的发展趋势和机遇...',
    createTime: '2025-06-10',
    status: '审核中'
  }
]);

const filteredList = computed(() => {
  const keyword = searchKey.value.toLowerCase();
  return dynamicList.value.filter(item => 
    item.title.toLowerCase().includes(keyword) || 
    item.author.toLowerCase().includes(keyword)
  );
});

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredList.value.slice(start, end);
});

const handlePreview = (file: UploadFile) => {
  console.log('预览文件:', file);
};

const handleRemove = (file: UploadFile, files: UploadFiles) => {
  console.log('移除文件:', file, files);
};

const publishDynamic = () => {
  const newDynamic: Dynamic = {
    id: Date.now(),
    title: dynamicForm.title,
    content: dynamicForm.content,
    intro: dynamicForm.intro,
    author: dynamicForm.author,
    createTime: new Date().toISOString().split('T')[0],
    status: '审核中',
    image: dynamicForm.image
  };
  
  dynamicList.value.unshift(newDynamic);
  resetForm();
  activeTab.value = 'list';
};

const resetForm = () => {
  dynamicForm.title = '';
  dynamicForm.content = '';
  dynamicForm.intro = '';
  dynamicForm.author = '';
  dynamicForm.image = '';
  fileList.value = [];
};

const viewDetail = (row: Dynamic) => {
  activeDynamic.value = row;
  activeTab.value = 'detail';
};

const editDynamic = (row: Dynamic) => {
  Object.assign(dynamicForm, {
    title: row.title,
    content: row.content,
    intro: row.intro,
    author: row.author,
    image: row.image
  });
  activeTab.value = 'publish';
};

const deleteDynamic = (row: Dynamic) => {
  dynamicList.value = dynamicList.value.filter(item => item.id !== row.id);
};

const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
};

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
};
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
.info {
  margin: 10px 0;
  color: #606266;
}

.content {
  margin-top: 20px;
  line-height: 1.8;
}
</style>