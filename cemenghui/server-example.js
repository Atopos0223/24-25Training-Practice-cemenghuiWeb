const express = require('express');
const cors = require('cors');
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');

const app = express();
const PORT = 8080;

// 中间件
app.use(cors());
app.use(express.json());

// 数据库连接配置
const dbConfig = {
  host: 'localhost',
  user: 'root', // 根据你的MySQL用户名修改
  password: '', // 根据你的MySQL密码修改
  database: 'cemenghui' // 使用你的实际数据库名
};

// 创建数据库连接池
const pool = mysql.createPool(dbConfig);

// 注册接口
app.post('/register', async (req, res) => {
  try {
    const { username, email, password, companyInfo, nickname, phone, gender } = req.body;
    
    // 验证必填字段
    if (!username || !password || !companyInfo || !nickname) {
      return res.status(400).json({
        code: 400,
        message: '请填写所有必填字段（用户名、密码、企业信息、昵称）'
      });
    }
    
    // 验证邮箱格式（如果提供了邮箱）
    if (email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        return res.status(400).json({
          code: 400,
          message: '邮箱格式不正确'
        });
      }
    }
    
    // 验证密码长度
    if (password.length < 6) {
      return res.status(400).json({
        code: 400,
        message: '密码长度不能少于6个字符'
      });
    }
    
    // 检查用户名是否已存在
    const [existingUsers] = await pool.execute(
      'SELECT id FROM user WHERE username = ?',
      [username]
    );
    
    if (existingUsers.length > 0) {
      return res.status(409).json({
        code: 409,
        message: '用户名已存在，请使用其他用户名'
      });
    }
    
    // 检查邮箱是否已存在（如果提供了邮箱）
    if (email) {
      const [existingEmails] = await pool.execute(
        'SELECT id FROM user WHERE email = ?',
        [email]
      );
      
      if (existingEmails.length > 0) {
        return res.status(409).json({
          code: 409,
          message: '邮箱已存在，请使用其他邮箱'
        });
      }
    }
    
    // 加密密码
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    
    // 生成用户ID（使用时间戳+随机数）
    const userId = Date.now() + Math.floor(Math.random() * 1000);
    
    // 插入用户数据到user表
    const [result] = await pool.execute(
      'INSERT INTO user (id, company, username, password, nickname, phone, email, gender, status, is_super, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())',
      [
        userId,
        companyInfo, // company字段存储企业信息
        username,
        hashedPassword,
        nickname,
        phone || null,
        email || null,
        gender || 1, // 默认性别为男
        1, // 默认状态为启用
        0  // 默认不是超级管理员
      ]
    );
    
    console.log('用户注册成功:', {
      id: userId,
      username,
      nickname,
      company: companyInfo,
      email,
      phone
    });
    
    res.status(200).json({
      code: 200,
      message: '注册成功',
      data: {
        userId: userId,
        username,
        nickname,
        company: companyInfo
      }
    });
    
  } catch (error) {
    console.error('注册错误:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误'
    });
  }
});

// 登录接口
app.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;
    
    // 验证必填字段
    if (!username || !password) {
      return res.status(400).json({
        code: 400,
        message: '用户名和密码不能为空'
      });
    }
    
    // 查询用户
    const [users] = await pool.execute(
      'SELECT id, company, username, password, nickname, phone, email, gender, status, is_super FROM user WHERE username = ?',
      [username]
    );
    
    if (users.length === 0) {
      return res.status(401).json({
        code: 401,
        message: '用户名或密码错误'
      });
    }
    
    const user = users[0];
    
    // 检查用户状态
    if (user.status === 0) {
      return res.status(403).json({
        code: 403,
        message: '账户已被禁用，请联系管理员'
      });
    }
    
    // 验证密码
    const isPasswordValid = await bcrypt.compare(password, user.password);
    
    if (!isPasswordValid) {
      return res.status(401).json({
        code: 401,
        message: '用户名或密码错误'
      });
    }
    
    console.log('用户登录成功:', {
      id: user.id,
      username: user.username,
      nickname: user.nickname
    });
    
    res.status(200).json({
      code: 200,
      message: '登录成功',
      data: {
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        company: user.company,
        phone: user.phone,
        email: user.email,
        gender: user.gender,
        isSuper: user.is_super === 1
      }
    });
    
  } catch (error) {
    console.error('登录错误:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误'
    });
  }
});

// 启动服务器
app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
  console.log('数据库连接配置:', {
    host: dbConfig.host,
    database: dbConfig.database,
    user: dbConfig.user
  });
});

// 测试数据库连接
pool.getConnection()
  .then(connection => {
    console.log('数据库连接成功！');
    connection.release();
  })
  .catch(err => {
    console.error('数据库连接失败:', err.message);
  });

// 数据库表结构示例
/*
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  company_info VARCHAR(200),
  user_type ENUM('enterprise', 'individual') DEFAULT 'enterprise',
  is_super TINYINT(1) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
*/ 