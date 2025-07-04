import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
  },
  {
    path: '/adminhome',
    name: 'adminhome',
    component: () => import('../views/AdminHomeView.vue'),
	children: [
		
	  {
	    path: 'info-management',
	    name: 'AdminInfoManagement',
	    component: () => import('@/views/admin/userManagement/InfoManagementView.vue'),
	    meta: { title: '信息管理' }
	  },
	  {
        path: 'permission-management',
        name: 'PermissionManagement',
        component: () => import('@/views/admin/userManagement/PermissionManagement.vue'),
        meta: { title: '权限管理' }
      },
	  {
        path: 'audit-news',
        name: 'AuditNews',
        component: () => import('@/views/admin/dynamicManagement/AuditNews.vue'),
        meta: { title: '审核资讯' }
      },
	  {
	    path: 'manage-dynamics',
	    component: () => import('@/views/admin/dynamicManagement/DynamicList.vue'),
	    meta: { title: '动态列表' }
	  },
	  {
	    path: 'publish',
	    name: 'AdminPublishDynamic',
	    component: () => import('../views/admin/dynamicManagement/PublishDynamic.vue'),
		meta: { title: '发布动态' }
	  },
	  {
	  
	    path: 'dynamic-detail/:id',
	    name: 'AdminDynamicDetail',
	    component: () => import('@/views/admin/dynamicManagement/DynamicDetail.vue'),
	    meta: { title: '动态详情' },
	    props: true
	  },
	  {
	    path: 'edit-dynamic/:id',
	    name: 'AdminEditDynamic',
	    component: () => import('@/views/admin/dynamicManagement/EditDynamic.vue'),
	    meta: { title: '编辑动态' },
	    props: true
	  },
	  {
        path: 'audit-courses',
        name: 'AuditCourses',
        component: () => import('@/views/admin/courseManagement/AuditCourses.vue'),
        meta: { title: '审核课程' }
      },
	  {

	  path: '/courses-management',
	    name: 'CoursesManagement',
	    component: () => import('@/views/admin/courseManagement/CoursesManagement.vue'),
	    meta: { title: '管理课程' }
	  },
	{
	path: 'courses-management/edit/:id',
	  name: 'EditCourse',
	  component: () => import('@/views/admin/courseManagement/EditCourse.vue'),
	  meta: { title: '编辑课程' },
	  props: true  // 允许将路由参数传递给组件
	},
	{
	  path: 'manage-courses',
	  component: () => import('@/views/admin/courseManagement/CourseList.vue'),
	  meta: { title: '课程列表' }
	},
	
	  {
    
        path: 'audit-meetings',

        name: 'AuditMeetings',
        component: () => import('@/views/admin/meetingManagement/AuditMeetings.vue'),
        meta: { title: '审核会议' }
      },
	  {
        path: 'create-meeting',
        name: 'CreateMeeting',
        component: () => import('@/views/admin/meetingManagement/CreateMeeting.vue'),
        meta: { title: '创建会议' }
      },
	  
	  
	  
	  {
	    path: 'manage-meetings',
	    component: () => import('@/views/admin/meetingManagement/MeetingList.vue'),
	    meta: { title: '会议列表' }
	  },
	  
      {
        path: '/adminhome/meetingmanage/edit',
        name: 'AdminEditMeeting',
        component: () => import('@/views/admin/meetingManagement/EditMeeting.vue')
      },
	  {
        path: '/adminhome/meetingmanage/detail/:id',
        name: 'AdminMeetingDetail',
        component: () => import('@/views/admin/meetingManagement/MeetingDetail.vue'),
        meta: { title: '会议详情' },
        props: true
      },
	  {
        path: '/adminhome/meetingmanage/create',
        name: 'AdminCreateMeeting',
        component: () => import('@/views/admin/meetingManagement/CreateMeeting.vue'),
        meta: { title: '创建会议' }
      },

	]
  },
  
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
  },
  {
    path: '/userhome',
    name: 'userhome',
    component: () => import('../views/UserHome.vue'),
    children: [
      {
        path: 'basic-info',
        name: 'basicInfo',
        component: () => import('@/views/user/userInfo/BasicInfo.vue'),
        meta: { title: '基本资料修改', defaultTab: 'base' }
      },
      {
        path: 'change-password',
        name: 'changePassword',
        component: () => import('@/views/user/userInfo/ChangePassword.vue'),
        meta: { title: '修改密码', defaultTab: 'password' }
      },
      {
        path: 'view-info',
        name: 'viewInfo',
        component: () => import('@/views/user/userInfo/ViewInfo.vue'),
        meta: { title: '查看信息', defaultTab: 'view' }
      },
      {
        path: 'industrydynamic',
        name: 'industrydynamic',
        children: [
          {
            path: 'publish',
            component: () => import('@/views/user/dynamic/PublishDynamic.vue'),
            meta: { title: '发布动态' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/dynamic/DynamicList.vue'),
            meta: { title: '动态列表' }
          },
          {
            path: 'detail/:id',
            name: 'DynamicDetail',
            component: () => import('@/views/user/dynamic/DynamicDetail.vue'),
            meta: { title: '动态详情' },
            props: true
          },
          {
            path: 'edit/:id',
            name: 'EditDynamic',
            component: () => import('@/views/user/dynamic/EditDynamic.vue'),
            meta: { title: '编辑动态' },
            props: true
          }
        ]
      },
      {
        path: 'coursemanage',
        name: 'coursemanage',
        children: [
          {
            path: 'add',
            component: () => import('@/views/user/course/Addcourse.vue'),
            meta: { title: '添加课程' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/course/CourseList.vue'),
            meta: { title: '课程列表' }
          },
          {
            path: 'audit',
            component: () => import('@/views/user/course/AuditManage.vue'),
            meta: { title: '审核管理' },
            props: true
          },
          {
            path: 'edit/:id',
            component: () => import('@/views/user/course/EditCourse.vue'),
            meta: { title: '编辑' },
            props: true
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/user/course/CourseDetail.vue'),
            meta: { title: '课程详情' },
            props: true
          }
        ]
      },
      {
        path: 'meetingmanage',
        name: 'meetingmanage',
        children: [
          {
            path: 'create',
            component: () => import('@/views/user/meeting/CreateMeeting.vue'),
            meta: { title: '创建会议' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/meeting/MeetingList.vue'),
            meta: { title: '会议列表' }
          },
          {
            path: 'audit',
            component: () => import('@/views/user/meeting/AuditMeeting.vue'),
            meta: { title: '审核管理' },
            props: true
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/user/meeting/MeetingDetail.vue'),
            meta: { title: '会议详情' },
            props: true
          },
          {
            path: 'edit',
            component: () => import('@/views/user/meeting/EditMeeting.vue'),
            meta: { title: '编辑会议' }
          }
        ]
      }
    ]
  },
  {
    path: '/find-password',
    name: 'findPassword',
    component: () => import('../views/FindPassword.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// 移除了全局前置守卫，不再进行权限验证

export default router;