// router/index.ts
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import UserHome from '../views/UserHome.vue';
// 引入子系统组件
import UserManageSystem from '../views/user/UserManageSystem.vue';
import IndustryDynamicSystem from '../views/user/IndustryDynamicSystem.vue';
import CourseManageSystem from '../views/user/CourseManageSystem.vue';
import MeetingManageSystem from '../views/user/MeetingManageSystem.vue';
import PublishDynamic from '@/views/user/PublishDynamic.vue';
import DynamicList from '@/views/user/DynamicDetail.vue';
import DynamicDetail from '@/views/user/DynamicDetail.vue';

// 定义路由数组
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'login', // 根路径默认显示登录页
    component: LoginView
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView
  },

  {
    path: '/user-home',
    name: 'userhome',
    component: UserHome,
        
   children: [
        {
          path: 'basic-info', // 基本资料修改
          name: 'basicInfo',
          component: () => import('@/views/user/BasicInfo.vue'),
          meta: { 
            title: '基本资料修改',
            defaultTab: 'base' // 传递给组件的参数
          }
        },
        {
          path: 'change-password', // 修改密码
          name: 'changePassword',
          component: () => import('@/views/user/ChangePassword.vue'),
          meta: { 
            title: '修改密码',
            defaultTab: 'password' 
          }
		  
        },
		
        {
          path: 'view-info', // 查看信息
          name: 'viewInfo',
          component: () => import('@/views/user/ViewInfo.vue'),
          meta: { 
            title: '查看信息',
            defaultTab: 'view' 
          }
        },
		
		
      {
        path: '/industry-dynamic',
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
        path: '/course-manage',
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
        path: '/meeting-manage',
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
	},
  ]
},
	  
	  
	  
	  
    ]
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes // 使用上面定义的路由数组
});

try {
} catch (error) {
  console.error('获取 token 时出现错误:', error);
}
// 全局前置守卫：验证登录状态
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token'); 
  if (to.path !== '/' && !token) {
    next('/'); // 未登录则跳转到登录页
  } else if (to.path === '/' && token) {
    next('/user-home'); // 已登录则跳转到主页，避免重复登录
  } else {
    next(); // 正常访问
  }
});

export default router;
