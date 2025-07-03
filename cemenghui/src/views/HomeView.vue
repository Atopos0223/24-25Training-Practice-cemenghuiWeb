<script setup lang="ts">

</script>

<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 首页</h2>
    <el-divider />
    <!-- 原有内容放这里 -->
    <div class="home-content">
      <!-- ... -->
    </div>
  </el-card>
</template>

<style scoped>
.main-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(64, 158, 255, 0.08);
  padding: 32px 24px;
  background: #fff;
  min-height: 300px;
  margin: 24px 0;
}
.main-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.home-content {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
}
</style>
