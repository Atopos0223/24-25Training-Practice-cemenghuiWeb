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
	    path: '/info-management',
	    name: 'InfoManagement',
	    component: () => import('../views/InfoManagementView.vue'),
	    // 移除了 requiresAdmin 元信息，因为不再需要权限验证
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
        component: () => import('@/views/user/BasicInfo.vue'),
        meta: { title: '基本资料修改', defaultTab: 'base' }
      },
      {
        path: 'change-password',
        name: 'changePassword',
        component: () => import('@/views/user/ChangePassword.vue'),
        meta: { title: '修改密码', defaultTab: 'password' }
      },
      {
        path: 'view-info',
        name: 'viewInfo',
        component: () => import('@/views/user/ViewInfo.vue'),
        meta: { title: '查看信息', defaultTab: 'view' }
      },
      {
        path: 'industrydynamic',
        name: 'industrydynamic',
        children: [
          {
            path: 'publish',
            component: () => import('@/views/user/PublishDynamic.vue'),
            meta: { title: '发布动态' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/DynamicList.vue'),
            meta: { title: '动态列表' }
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/user/DynamicDetail.vue'),
            meta: { title: '动态详情' },
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
            component: () => import('@/views/user/Addcourse.vue'),
            meta: { title: '添加课程' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/CourseList.vue'),
            meta: { title: '课程列表' }
          },
          {
            path: 'audit',
            component: () => import('@/views/user/AuditManage.vue'),
            meta: { title: '审核管理' },
            props: true
          },
          {
            path: 'edit/:id',
            component: () => import('@/views/user/EditCourse.vue'),
            meta: { title: '编辑' },
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
            component: () => import('@/views/user/CreateMeeting.vue'),
            meta: { title: '创建会议' }
          },
          {
            path: 'list',
            component: () => import('@/views/user/MeetingList.vue'),
            meta: { title: '会议列表' }
          },
          {
            path: 'audit',
            component: () => import('@/views/user/AuditMeeting.vue'),
            meta: { title: '审核管理' },
            props: true
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/user/MeetingDetail.vue'),
            meta: { title: '会议详情' },
            props: true
          }
        ]
      }
    ]
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// 移除了全局前置守卫，不再进行权限验证

export default router;