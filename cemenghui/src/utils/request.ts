import axios from 'axios';

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // 你的 API 基础路径
  timeout: 5000
});

// 请求拦截器：添加 token
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误', error);
    return Promise.reject(error);
  }
);

export default service;