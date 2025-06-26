import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    host: 'localhost', // 或 '0.0.0.0'，看需求
    port: 5173,
    open: true // 启动时自动开浏览器，方便测试
  },
   css: {
      postcss: undefined // 关闭 PostCSS 处理
    },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})