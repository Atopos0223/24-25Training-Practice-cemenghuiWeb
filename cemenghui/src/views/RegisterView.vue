<template>
  <div class="register-container">
    <div class="background"></div>
    
    <!-- 居中容器 -->
    <div class="center-wrapper">
      <el-card class="register-card" shadow="hover">
        <!-- 页面标题 -->
        <div slot="header" class="register-header">
          <h3>用户注册</h3>
          <p class="subtitle">欢迎加入测盟汇管理系统</p>
        </div>
        
        <!-- 注册表单 -->
        <el-form class="register-form" @submit.prevent="handleRegister">
          <!-- 用户名输入 -->
          <el-form-item label="用户名">
            <el-input 
              v-model="username" 
              placeholder="请输入用户名"
              size="large"
            ></el-input>
            <div class="input-tip">用户名长度不少于3个字符</div>
          </el-form-item>
          
          <!-- 昵称输入 -->
          <el-form-item label="昵称">
            <el-input 
              v-model="nickname" 
              placeholder="请输入昵称"
              size="large"
            ></el-input>
            <div class="input-tip">昵称长度不少于2个字符</div>
          </el-form-item>
          
          <!-- 邮箱输入 -->
          <el-form-item label="邮箱">
            <el-input 
              type="email" 
              v-model="email" 
              placeholder="请输入邮箱地址（可选）"
              size="large"
            ></el-input>
            <div class="input-tip">请输入有效邮箱地址（可选）</div>
          </el-form-item>
          
          <!-- 手机号输入 -->
          <el-form-item label="手机号">
            <el-input 
              v-model="phone" 
              placeholder="请输入手机号（可选）"
              size="large"
            ></el-input>
            <div class="input-tip">请输入11位手机号（可选）</div>
          </el-form-item>
          
          <!-- 企业信息输入 -->
          <el-form-item label="企业信息">
            <el-input 
              v-model="companyInfo" 
              placeholder="请输入企业全称"
              size="large"
            ></el-input>
            <div class="input-tip">企业全称不能为空</div>
          </el-form-item>
          
          <!-- 性别选择 -->
          <el-form-item label="性别">
            <el-radio-group v-model="gender" size="large">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="0">女</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 密码输入 -->
          <el-form-item label="密码">
            <el-input 
              type="password" 
              v-model="password" 
              placeholder="请输入密码"
              size="large"
            ></el-input>
            <div class="input-tip">密码长度不少于6个字符</div>
          </el-form-item>
          
          <!-- 确认密码输入 -->
          <el-form-item label="确认密码">
            <el-input 
              type="password" 
              v-model="confirmPassword" 
              placeholder="请再次输入密码"
              size="large"
            ></el-input>
            <div class="input-tip">两次输入需一致</div>
          </el-form-item>
          
          <!-- 注册按钮 -->
          <el-form-item class="form-actions">
            <el-button 
              type="primary" 
              size="large" 
              @click="handleRegister"
              :loading="loading"
              :disabled="loading"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
            <el-button 
              type="text" 
              size="large" 
              @click="goToLogin"
              :disabled="loading"
            >
              返回登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 成功提示弹窗 -->
    <div v-if="showSuccessModal" class="success-modal">
      <div class="modal-content">
        <div class="modal-title">注册成功</div>
        <div class="modal-message">注册成功！正在跳转到登录页面...</div>
      </div>
    </div>
    
    <!-- 错误提示弹窗 -->
    <div v-if="showErrorModal" class="error-modal">
      <div class="modal-content">
        <div class="modal-title">注册失败</div>
        <div class="modal-message">{{ errorMessage }}</div>
        <div class="modal-actions">
          <button @click="closeErrorModal">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  name: "Register",
  data() {
    return {
      username: "",
      nickname: "",
      email: "",
      phone: "",
      password: "",
      confirmPassword: "",
      companyInfo: "",
      gender: 1, // 默认选择男性
      usernameError: "",
      nicknameError: "",
      emailError: "",
      phoneError: "",
      passwordError: "",
      confirmPasswordError: "",
      companyInfoError: "",
      showSuccessModal: false,
      showErrorModal: false,
      errorMessage: "",
      loading: false
    };
  },
  methods: {
    // 清除错误信息
    clearErrors() {
      this.usernameError = "";
      this.nicknameError = "";
      this.emailError = "";
      this.phoneError = "";
      this.passwordError = "";
      this.confirmPasswordError = "";
      this.companyInfoError = "";
    },
    // 验证表单
    validateForm() {
      let isValid = true;
      
      // 验证用户名
      if (!this.username) {
        this.usernameError = "用户名不能为空";
        isValid = false;
      } else if (this.username.length < 3) {
        this.usernameError = "用户名长度不能少于3个字符";
        isValid = false;
      } else {
        this.usernameError = "";
      }
      
      // 验证昵称
      if (!this.nickname) {
        this.nicknameError = "昵称不能为空";
        isValid = false;
      } else if (this.nickname.length < 2) {
        this.nicknameError = "昵称长度不能少于2个字符";
        isValid = false;
      } else {
        this.nicknameError = "";
      }
      
      // 验证邮箱格式（如果提供了邮箱）
      if (this.email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.email)) {
          this.emailError = "请输入有效的邮箱地址";
          isValid = false;
        } else {
          this.emailError = "";
        }
      } else {
        this.emailError = "";
      }
      
      // 验证手机号格式（如果提供了手机号）
      if (this.phone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        if (!phoneRegex.test(this.phone)) {
          this.phoneError = "请输入有效的手机号";
          isValid = false;
        } else {
          this.phoneError = "";
        }
      } else {
        this.phoneError = "";
      }
      
      // 验证企业信息
      if (!this.companyInfo) {
        this.companyInfoError = "企业信息不能为空";
        isValid = false;
      } else {
        this.companyInfoError = "";
      }
      
      // 验证密码
      if (!this.password) {
        this.passwordError = "密码不能为空";
        isValid = false;
      } else if (this.password.length < 6) {
        this.passwordError = "密码长度不能少于6个字符";
        isValid = false;
      } else {
        this.passwordError = "";
      }
      
      // 验证确认密码
      if (!this.confirmPassword) {
        this.confirmPasswordError = "请确认密码";
        isValid = false;
      } else if (this.password !== this.confirmPassword) {
        this.confirmPasswordError = "两次输入的密码不一致";
        isValid = false;
      } else {
        this.confirmPasswordError = "";
      }
      
      return isValid;
    },
    // 处理注册
    async handleRegister() {
      if (!this.validateForm()) {
        this.errorMessage = "注册信息格式有误，请检查各项输入";
        this.showErrorModal = true;
        return;
      }
      
      this.loading = true;
      
      try {
        const response = await axios.post('http://localhost:8080/register', {
          username: this.username,
          email: this.email,
          password: this.password,
          companyInfo: this.companyInfo,
          type: 'enterprise', // 默认为企业用户
          nickname: this.nickname,
          phone: this.phone,
          gender: this.gender
        });
        
        console.log('注册响应:', response);
        
        if (response.data && response.data.code === 200) {
          // 注册成功
          this.showSuccessModal = true;
          ElMessage.success('注册成功！');
          
          // 3秒后跳转到登录页面
          setTimeout(() => {
            this.$router.push("/login");
          }, 3000);
        } else {
          // 注册失败
          this.errorMessage = response.data?.message || "注册失败，请稍后重试";
          this.showErrorModal = true;
          ElMessage.error(this.errorMessage);
        }
      } catch (error) {
        console.error('注册请求错误:', error);
        
        if (error.response) {
          console.log('响应状态码:', error.response.status);
          console.log('响应数据:', error.response.data);
          
          // 根据不同的错误状态码显示不同的错误信息
          if (error.response.status === 409) {
            this.errorMessage = "用户名或邮箱已存在，请使用其他信息";
          } else if (error.response.status === 400) {
            this.errorMessage = error.response.data?.message || "请求参数错误";
          } else if (error.response.status === 500) {
            this.errorMessage = "服务器内部错误，请稍后重试";
          } else {
            this.errorMessage = error.response.data?.message || "注册失败，请稍后重试";
          }
        } else if (error.request) {
          this.errorMessage = "网络连接失败，请检查网络连接";
        } else {
          this.errorMessage = "注册失败，请稍后重试";
        }
        
        this.showErrorModal = true;
        ElMessage.error(this.errorMessage);
      } finally {
        this.loading = false;
      }
    },
    // 关闭错误弹窗
    closeErrorModal() {
      this.showErrorModal = false;
    },
    // 跳转到登录页面
    goToLogin() {
      this.$router.push("/login");
    }
  }
};
</script>

<style scoped>
.register-container {
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

.register-card {
  width: 100%;
  max-width: 500px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.register-header h3 {
  font-size: 24px;
  color: #303133;
  font-weight: 600;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.register-form {
  padding: 20px 30px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.success-modal,
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  max-width: 400px;
  width: 90%;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.modal-message {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.modal-actions {
  display: flex;
  justify-content: center;
}

.modal-actions button {
  padding: 8px 20px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.modal-actions button:hover {
  background-color: #40a9ff;
}

.success-modal .modal-title {
  color: #52c41a;
}

.error-modal .modal-title {
  color: #ff4d4f;
}

/* 响应式处理 */
@media (max-width: 576px) {
  .register-card {
    max-width: 90%;
  }
  
  .register-form {
    padding: 15px;
  }
  
  .background {
    filter: blur(2px);
  }
}

.input-tip {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
  margin-left: 2px;
}
</style>
