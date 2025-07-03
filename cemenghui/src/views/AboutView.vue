<template>
  <el-card class="main-card" shadow="hover">
    <h2 class="main-title"><el-icon><User /></el-icon> 关于我们</h2>
    <el-divider />
    <div class="about-content">
      <h1>This is an about page</h1>
    </div>
  </el-card>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}

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
.about-content {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
}
</style>
