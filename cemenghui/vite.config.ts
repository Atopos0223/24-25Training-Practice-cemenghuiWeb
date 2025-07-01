import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    host: 'localhost',
    port: 5173,
    open: true,
    // 新增代理配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端 Spring Boot 地址
        changeOrigin: true,               // 允许跨域
      }
    }
  },
  css: {
    postcss: undefined
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
