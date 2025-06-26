<template>
  <div class="basic-info">
    <el-form :model="form" label-width="120px">
      <el-form-item label="昵称">
        <el-input v-model="form.nickname"></el-input>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
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
  nickname: '',
  phone: '',
  email: '',
  gender: '男'
})

const submit = () => {
  console.log('保存基本信息:', form)
  // 调用API保存数据
}
</script>

<style scoped>
.basic-info {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>