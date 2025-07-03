<template>
  <div class="login-bg">
    <el-card class="login-card" shadow="hover">
      <h2 class="main-title"><el-icon><User /></el-icon> 用户登录</h2>
      <el-divider />
      <el-form :model="loginForm" label-width="90px" class="login-form">
        <el-form-item label="账号">
          <el-input v-model="uname" size="large"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="upwd" size="large"></el-input>
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button type="primary" size="large" @click="handleLogin">登录</el-button>
          <el-button type="text" size="large" @click="handleRegister">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="forgot-password-link">
        <a @click="handleFindPassword" style="cursor:pointer; color:#409EFF;">忘记密码？</a>
      </div>
    </el-card>
  </div>
</template>

<script>
import { User, Lock } from '@element-plus/icons-vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { ref, defineComponent } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    const router = useRouter();
    const uname = ref('');
    const upwd = ref('');
    const loading = ref(false);
    
    // 登录表单对象
    const loginForm = ref({
      username: '',
      password: ''
    });
    const handleLogin = async () => {
      loading.value = true;
      try {
        const res = await axios.post('http://localhost:8080/login', {
          username: uname.value,
          password: upwd.value,
        });
        
        // 打印完整的响应对象
        console.log('完整响应对象:', res);
        // 打印响应数据
        console.log('响应数据:', res.data);
        // 打印code属性（注意可能的正确位置）
        console.log('res.code:', res.code);
        console.log('res.data.code:', res.data.code);
        
        if (res.data && res.data.code === 200) {
          // 存储用户信息到localStorage
          const userData = res.data.data;
          localStorage.setItem('userInfo', JSON.stringify({
            id: userData.id,
            username: userData.username,
            nickname: userData.nickname || userData.username,
            phone: userData.phone || '',
            email: userData.email || '',
            gender: userData.gender === 1 ? '男' : userData.gender === 2 ? '女' : '男',
            is_super: userData.is_super,
            company: userData.company || ''
          }));
          localStorage.setItem('userId', userData.id);
          
          if (userData.is_super === 1) {  // 注意：可能需要访问 res.data.data
            router.push('/adminhome');
          } else {
            router.push('/userhome'); 
          }
        } else {
          ElMessage.error(res.data ? res.data.message : '未知错误');
        }
      } catch (error) {
        console.error('请求错误:', error);
        if (error.response) {
          console.log('响应状态码:', error.response.status);
          console.log('响应数据:', error.response.data);
        }
        ElMessage.error('网络异常，请重试');
      } finally {
        loading.value = false;
      } 
    };
    
    
    // 处理注册
    const handleRegister = () => {
      router.push('/register');
    };

    // 处理找回密码
    const handleFindPassword = () => {
      router.push('/find-password');
    };

    return {
      uname,
      upwd,
      loading,
      loginForm,
      handleLogin,
      handleRegister,
      handleFindPassword
    };
  }
});
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #409EFF 0%, #53c0ff 100%);
}
.login-card {
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(64, 158, 255, 0.12);
  padding: 40px 32px;
  background: #fff;
  min-width: 380px;
}
.main-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.el-form-item {
  margin-bottom: 24px;
}
.el-input {
  border-radius: 8px;
}
.el-button {
  border-radius: 24px;
  font-size: 16px;
  padding: 8px 32px;
  transition: background 0.2s;
}
.el-button:hover {
  background: #53c0ff;
  color: #fff;
}
.forgot-password-link {
  text-align: right;
  margin-top: 8px;
}
</style>