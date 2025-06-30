<template>
  <div class="login-container">
    <div class="background"></div>
    
    <!-- 居中容器 -->
    <div class="center-wrapper">
      <el-card class="login-card" shadow="hover">
        <div slot="header" class="login-header">
          <h3>测盟汇管理系统</h3>
        </div>
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
/* 样式部分保持不变 */
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://picsum.photos/id/1048/1920/1080') center/cover no-repeat;
  filter: blur(3px);
  z-index: 1;
}

.center-wrapper {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 450px; /* 增大登录框宽度 */
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h3 {
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.login-form {
  padding: 20px 30px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式处理 */
@media (max-width: 576px) {
  .login-card {
    max-width: 90%;
  }
  
  .login-form {
    padding: 15px;
  }
  
  .background {
    filter: blur(2px);
  }
}

.forgot-password-link {
  text-align: right;
  margin-top: 8px;
}
</style>