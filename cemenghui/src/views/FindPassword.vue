<template>
  <div class="find-password-container">
    <h2>找回密码</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">用户名</label>
        <input v-model="form.username" id="username" type="text" required />
      </div>
      <div class="form-group">
        <label for="phone">手机号</label>
        <input v-model="form.phone" id="phone" type="text" required />
      </div>
      <div class="form-group">
        <label for="newPassword">新密码</label>
        <input v-model="form.newPassword" id="newPassword" type="password" required />
      </div>
      <button type="submit">提交</button>
    </form>
    <div v-if="message" :class="{'success': success, 'error': !success}">{{ message }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const form = ref({
  username: '',
  phone: '',
  newPassword: ''
})
const message = ref('')
const success = ref(false)

const handleSubmit = async () => {
  message.value = ''
  try {
    const res = await request.post('/findPassword', form.value)
    if (res.data && res.data.code === 200) {
      message.value = '密码重置成功，请返回登录页登录！'
      success.value = true
      setTimeout(() => router.push('/login'), 1500)
    } else {
      message.value = res.data ? res.data.message : '重置失败'
      success.value = false
    }
  } catch (e) {
    message.value = '请求失败，请稍后重试'
    success.value = false
  }
}
</script>

<style scoped>
.find-password-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
.form-group {
  margin-bottom: 18px;
}
label {
  display: block;
  margin-bottom: 6px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}
.success {
  color: #42b983;
  margin-top: 12px;
}
.error {
  color: #d32f2f;
  margin-top: 12px;
}
</style> 