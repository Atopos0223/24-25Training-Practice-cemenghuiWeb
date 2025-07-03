<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><Calendar /></el-icon> 会议管理系统</h2>
    <el-divider />
    <div class="meeting-manage-system">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="创建会议" name="create">
          <el-form :model="meetingForm" label-width="120px">
            <el-form-item label="会议名称">
              <el-input v-model="meetingForm.name"></el-input>
            </el-form-item>
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="meetingForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="meetingForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="创建人">
              <el-input v-model="meetingForm.creator"></el-input>
            </el-form-item>
            <el-form-item label="会议内容">
              <el-input type="textarea" v-model="meetingForm.content"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="createMeeting">创建</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="会议列表" name="list">
          <el-row class="filter-row">
            <el-col :span="6">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              ></el-date-picker>
            </el-col>
            <el-col :span="6">
              <el-input v-model="searchName" placeholder="会议名称"></el-input>
            </el-col>
            <el-col :span="6">
              <el-input v-model="searchCreator" placeholder="创建人"></el-input>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="filterMeetings">筛选</el-button>
            </el-col>
          </el-row>
          <el-table :data="meetingList" border style="width: 100%">
            <el-table-column prop="id" label="序号" width="80" />
            <el-table-column prop="name" label="会议名称" min-width="120" />
            <el-table-column prop="creator" label="创建人" width="100" />
            <el-table-column prop="startTime" label="开始时间" width="160" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="360">
              <template #default="scope">
                <div class="button-row">
                  <el-button type="primary" size="small" @click="viewMeeting(scope.row)">查看</el-button>
                  <el-button type="warning" size="small" @click="editMeeting(scope.row)" v-if="scope.row.status === '未审核'">编辑</el-button>
                  <el-button type="danger" size="small" @click="deleteMeeting(scope.row)" v-if="scope.row.status === '未审核'">删除</el-button>
                  <el-button type="success" size="small" @click="submitAudit(scope.row)" v-if="scope.row.status === '未审核'">提交审核</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="会议详情" name="detail" v-if="activeMeeting">
          <el-card>
            <h3>{{ activeMeeting.name }}</h3>
            <el-descriptions column="2" border>
              <el-descriptions-item label="开始时间">{{ formatDate(activeMeeting.startTime) }}</el-descriptions-item>
              <el-descriptions-item label="结束时间">{{ formatDate(activeMeeting.endTime) }}</el-descriptions-item>
              <el-descriptions-item label="创建人">{{ activeMeeting.creator }}</el-descriptions-item>
              <el-descriptions-item label="状态">{{ activeMeeting.status }}</el-descriptions-item>
              <el-descriptions-item label="会议内容" :span="2">{{ activeMeeting.content }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-tab-pane>
        
        <el-tab-pane label="审核管理" name="audit">
          <el-table :data="auditList" border style="width: 100%">
            <el-table-column prop="name" label="会议名称"></el-table-column>
            <el-table-column prop="creator" label="创建人"></el-table-column>
            <el-table-column prop="submitTime" label="提交时间"></el-table-column>
            <el-table-column prop="status" label="审核状态"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="viewMeeting(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import dayjs, { Dayjs } from 'dayjs';

interface Meeting {
  id: number;
  name: string;
  startTime: string | Date;
  endTime: string | Date;
  creator: string;
  content: string;
  status: '已发布' | '未审核' | '审核中' | '已通过' | '未通过';
  submitTime?: string;
}

const activeTab = ref<'create' | 'list' | 'detail' | 'audit'>('create');
const activeMeeting = ref<Meeting | null>(null);
const dateRange = ref<[Dayjs, Dayjs]>();
const searchName = ref('');
const searchCreator = ref('');

const meetingForm = reactive({
  name: '',
  startTime: '',
  endTime: '',
  creator: '',
  content: ''
});

const meetingList = ref<Meeting[]>([
  {
    id: 1,
    name: '2025年第二季度教学研讨会',
    startTime: '2025-06-30T09:00:00',
    endTime: '2025-06-30T11:30:00',
    creator: '王主任',
    content: '讨论第二季度教学成果和第三季度教学计划...',
    status: '已发布'
  },
  {
    id: 2,
    name: '职业教育课程开发会议',
    startTime: '2025-07-10T14:00:00',
    endTime: '2025-07-10T16:00:00',
    creator: '李老师',
    content: '讨论职业教育新课程开发方案...',
    status: '未审核'
  }
]);

const auditList = ref<Meeting[]>([
  {
    id: 3,
    name: '职业教育课程开发会议',
    creator: '李老师',
    startTime: '2025-07-10T14:00:00',
    endTime: '2025-07-10T16:00:00',
    content: '讨论职业教育新课程开发方案...',
    submitTime: '2025-06-22',
    status: '审核中'
  }
]);

const filteredMeetings = computed(() => {
  return meetingList.value.filter(meeting => {
	   const meetingDate = dayjs(meeting.startTime);
    const nameMatch = meeting.name.includes(searchName.value);
    const creatorMatch = meeting.creator.includes(searchCreator.value);
    let dateMatch = true;
    
    if (dateRange.value) {
      const [start, end] = dateRange.value;
    dateMatch = meetingDate.isAfter(start) && meetingDate.isBefore(end);
    }
    
    return nameMatch && creatorMatch && dateMatch;
  });
});

const formatDate = (dateStr: string | Date) => {
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const createMeeting = () => {
  const newMeeting: Meeting = {
    id: Date.now(),
    name: meetingForm.name,
    startTime: meetingForm.startTime,
    endTime: meetingForm.endTime,
    creator: meetingForm.creator,
    content: meetingForm.content,
    status: '未审核'
  };
  
  meetingList.value.push(newMeeting);
  resetForm();
  activeTab.value = 'list';
};

const resetForm = () => {
  meetingForm.name = '';
  meetingForm.startTime = '';
  meetingForm.endTime = '';
  meetingForm.creator = '';
  meetingForm.content = '';
};

const viewMeeting = (row: Meeting) => {
  activeMeeting.value = row;
  activeTab.value = 'detail';
};

const editMeeting = (row: Meeting) => {
  Object.assign(meetingForm, {
    name: row.name,
    startTime: row.startTime,
    endTime: row.endTime,
    creator: row.creator,
    content: row.content
  });
  activeTab.value = 'create';
};

const deleteMeeting = (row: Meeting) => {
  meetingList.value = meetingList.value.filter(item => item.id !== row.id);
};

const submitAudit = (row: Meeting) => {
  row.status = '审核中';
  auditList.value.push({
    ...row,
    submitTime: new Date().toISOString().split('T')[0]
  });
};

const filterMeetings = () => {
  // 计算属性会自动更新，无需额外逻辑
  console.log('当前筛选结果:', filteredMeetings.value);
};
</script>


<style scoped>
.filter-row {
  margin-bottom: 15px;
  gap: 10px;
}
.button-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
}
.el-table .el-table__cell {
  padding: 12px 16px;
}
</style>