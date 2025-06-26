import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/LoginView.vue'),
    },
	{
		path:'/adminHomeView',
		name:'adminhome',
		component: () => import('../views/AdminHomeView.vue'),
	},
	{
	  path: '/info-management',
	  name: 'InfoManagement',
	  component: () => import('../views/InfoManagementView.vue'),
	  meta: { requiresAdmin: true }
	}
  ],
})

export default router
