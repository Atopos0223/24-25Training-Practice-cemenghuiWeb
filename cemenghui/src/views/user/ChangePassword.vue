<template>
  <div class="change-password">
    <el-form :model="form" label-width="120px">
      <el-form-item label="旧密码">
        <el-input type="password" v-model="form.oldPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input type="password" v-model="form.newPassword"></el-input>
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input type="password" v-model="form.confirmPassword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const submit = () => {
  console.log('修改密码:', form)
  // 调用API修改密码
}
</script>

<style scoped>
.change-password {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>