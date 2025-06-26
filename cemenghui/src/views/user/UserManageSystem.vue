<template>
  <div class="user-manage-system">
    <h2>Web端登录和用户管理子系统</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="注册" name="register">
        <el-form :model="registerForm" label-width="120px">
          <el-form-item label="企业名称">
            <el-input v-model="registerForm.companyName"></el-input>
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="registerForm.contact"></el-input>
          </el-form-item>
          <el-form-item label="账号">
            <el-input v-model="registerForm.account"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" v-model="registerForm.password"></el-input>
          </el-form-item>
          <el-form-item label="验证码">
            <el-input v-model="registerForm.captcha"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleRegister">注册</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="登录" name="login">
        <el-form :model="loginForm" label-width="120px">
          <el-form-item label="账号">
            <el-input v-model="loginForm.account"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" v-model="loginForm.password"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="个人信息管理" name="info">
        <el-tabs v-model="infoTab">
          <el-tab-pane label="基本资料修改" name="base">
            <el-form :model="userInfo" label-width="120px">
              <el-form-item label="昵称">
                <el-input v-model="userInfo.nickname"></el-input>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email"></el-input>
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveBaseInfo">保存</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" label-width="120px">
              <el-form-item label="旧密码">
                <el-input type="password" v-model="passwordForm.oldPassword"></el-input>
              </el-form-item>
              <el-form-item label="新密码">
                <el-input type="password" v-model="passwordForm.newPassword"></el-input>
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input type="password" v-model="passwordForm.confirmPassword"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="savePassword">保存</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="查看信息" name="view">
            <el-card>
              <el-descriptions column="2" border>
                <el-descriptions-item label="昵称">{{ userInfo.nickname }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
                <el-descriptions-item label="创建日期">2025-01-01</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';

const activeTab = ref('register');
const infoTab = ref('base');

const registerForm = reactive({
  companyName: '',
  contact: '',
  account: '',
  password: '',
  captcha: ''
});

const loginForm = reactive({
  account: '',
  password: ''
});

const userInfo = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: '男'
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const handleRegister = () => {
  console.log('注册信息:', registerForm);
  // TODO: 调用注册API
};

const handleLogin = () => {
  console.log('登录信息:', loginForm);
  // TODO: 调用登录API
};

const saveBaseInfo = () => {
  console.log('保存基本信息:', userInfo);
  // TODO: 调用保存API
};

const savePassword = () => {
  console.log('修改密码:', passwordForm);
  // TODO: 调用修改密码API
};
</script>

<style scoped>
/* 自定义样式 */
</style>