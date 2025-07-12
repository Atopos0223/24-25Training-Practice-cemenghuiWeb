// LoginPage.ets
import { router } from '@kit.ArkUI';
import { BusinessError } from '@ohos.base';
import { http } from '@kit.NetworkKit';
import { preferences } from '@kit.ArkData';
import common from '@ohos.app.ability.common';

// 用户信息接口
interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  phone: string;
  email: string;
  gender: number;
  is_super: number;
  company: string;
}

// HTTP响应接口
interface ApiResponse {
  code: number;
  message: string;
  data: UserInfo;
}

// HTTP请求配置接口
interface HttpRequestOptions {
  method: http.RequestMethod;
  header: Record<string, string>;
  extraData: string;
}

// 路由选项接口 - 修复问题点
interface RouterOptions {
  url: string;
  params?: Record<string, string | number | boolean>;
}

@Entry
@Component
struct LoginPage {
  // 获取Ability上下文
  private context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext;

  @State username: string = '';
  @State password: string = '';
  @State loginStatus: string = '';
  @State isLoading: boolean = false; // 加载状态

  build() {
    Column() {
      // 顶部应用标题
      Text('测盟汇')
        .fontSize(36)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 80, bottom: 50 })
        .fontColor('#1a73e8')

      // 登录表单
      Column() {
        // 用户名输入
        TextInput({ placeholder: '请输入用户名/手机号' })
          .width('90%')
          .height(50)
          .backgroundColor(Color.White)
          .margin({ bottom: 20 })
          .borderRadius(10)
          .onChange((value: string) => {
            this.username = value;
          })

        // 密码输入
        TextInput({ placeholder: '请输入密码' })
          .width('90%')
          .height(50)
          .type(InputType.Password)
          .backgroundColor(Color.White)
          .margin({ bottom: 30 })
          .borderRadius(10)
          .onChange((value: string) => {
            this.password = value;
          })

        // 登录按钮
        Button('登录', { type: ButtonType.Capsule })
          .width('90%')
          .height(50)
          .backgroundColor(this.isLoading ? '#cccccc' : '#1a73e8')
          .fontColor(Color.White)
          .fontSize(18)
          .margin({ bottom: 20 })
          .enabled(!this.isLoading) // 加载时禁用按钮
          .onClick(() => {
            this.handleLogin();
          })

        // 加载指示器
        if (this.isLoading) {
          LoadingProgress()
            .color('#1a73e8')
            .width(40)
            .height(40)
            .margin({ top: 20 })
        }
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)

      // 状态提示
      Text(this.loginStatus)
        .fontSize(14)
        .fontColor(this.loginStatus.includes('成功') ? '#28a745' : '#dc3545')
        .margin({ top: 20 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f8f9fa')
  }

  // 登录处理
  private async handleLogin() {
    if (!this.username.trim()) {
      this.loginStatus = '请输入用户名或手机号';
      return;
    }

    if (this.password.length < 6) {
      this.loginStatus = '密码长度至少6位';
      return;
    }

    this.isLoading = true;
    this.loginStatus = '登录中...';

    try {
      // 创建HTTP请求
      let httpRequest = http.createHttp();

      // 请求URL（替换为实际后端地址）
      let url = 'http://localhost:8080/login'; // 模拟器使用10.0.2.2访问本机

      // 定义登录参数类型
      interface LoginParams {
        username: string;
        password: string;
      }

      // 创建类型化的请求参数
      const params: LoginParams = {
        username: this.username,
        password: this.password
      };

      // 创建类型化的请求配置
      const requestOptions: HttpRequestOptions = {
        method: http.RequestMethod.POST,
        header: {
          'Content-Type': 'application/json'
        },
        extraData: JSON.stringify(params) // 将参数转为JSON字符串
      };

      // 发送POST请求
      let response = await httpRequest.request(
        url,
        requestOptions
      );

      // 处理响应
      if (response.responseCode === http.ResponseCode.OK) {
        // 解析响应数据
        const result: ApiResponse = JSON.parse(response.result as string);
        console.info('登录响应:', JSON.stringify(result));

        if (result.code === 200) {
          // 存储用户信息
          const userData: UserInfo = result.data;
          await this.saveUserInfo(userData);

          this.loginStatus = '登录成功';
          router.pushUrl({ url: 'pages/MainPage' });

        } else {
          this.loginStatus = result.message || '登录失败';
        }
      } else {
        this.loginStatus = `服务器错误: ${response.responseCode}`;
      }
    } catch (error) {
      const err = error as BusinessError;
      console.error('请求错误:', JSON.stringify(err));
      this.loginStatus = '网络异常，请重试';
    } finally {
      this.isLoading = false;
    }
  }

  // 保存用户信息到Preferences
  private async saveUserInfo(userInfo: UserInfo): Promise<void> {
    try {
      const preferencesKey = 'userInfo';

      // 创建Preferences实例
      let preference = await preferences.getPreferences(this.context, 'userData');

      // 保存用户信息
      await preference.put(preferencesKey, JSON.stringify(userInfo));

      // 持久化数据
      await preference.flush();

      console.info('用户信息保存成功');
    } catch (err) {
      const error = err as BusinessError;
      console.error('保存用户信息失败:', JSON.stringify(error));
    }
  }
}