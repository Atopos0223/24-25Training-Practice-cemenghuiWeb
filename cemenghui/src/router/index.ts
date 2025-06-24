// router/index.ts
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import UserHome from '../views/UserHome.vue';
// 引入子系统组件
import UserManageSystem from '../components/UserManageSystem.vue';
import IndustryDynamicSystem from '../components/IndustryDynamicSystem.vue';
import CourseManageSystem from '../components/CourseManageSystem.vue';
import MeetingManageSystem from '../components/MeetingManageSystem.vue';

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
	  redirect: '/user-home/user-manage', // 添加这行设置默认跳转
    children: [
      {
        path: 'user-manage',
        name: 'usermanage',
		 component: () => import('@/components/UserManageSystem.vue'),
		     meta: { title: '用户管理' }
      },
      {
        path: 'industry-dynamic',
        name: 'industrydynamic',
      component: () => import('@/components/IndustryDynamicSystem.vue'),
	    meta: { title: '行业动态' }
      },
      {
        path: 'course-manage',
        name: 'coursemanage',
     component: () => import('@/components/CourseManageSystem.vue'),
	  meta: { title: '课程管理' }
      },
      {
        path: 'meeting-manage',
        name: 'meetingmanage',
      component: () => import('@/components/MeetingManageSystem.vue'),
	  meta: { title: '会议管理' }
      }
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
