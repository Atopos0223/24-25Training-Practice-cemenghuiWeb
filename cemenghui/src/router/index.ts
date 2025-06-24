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
	}
  ],
})

export default router
