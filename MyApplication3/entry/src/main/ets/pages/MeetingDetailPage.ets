// MeetingDetailPage.ets
import { router } from '@kit.ArkUI';
import { http } from '@kit.NetworkKit';

// 根据数据库结构定义会议详情模型
interface MeetingDetail {
  id: number;                // 会议ID
  title: string;             // 会议标题
  start_time: string;        // 开始时间
  end_time: string;          // 结束时间
  cover?: string;            // 封面图(可选)
  status: number;            // 状态
  location: string;          // 地点
  content: string;       // 描述
  create_time: string;       // 创建时间
  update_time?: string;      // 更新时间(可选)
  organizer: string;         // 组织者
  participants: number;      // 参与人数
  is_attending: boolean;     // 当前用户是否参加
}

// API响应接口
interface ApiResponse {
  code: number;
  message: string;
  data: MeetingDetail;
}

// 参会响应接口
interface AttendResponse {
  code: number;
  message: string;
}

// 参会请求数据接口
interface AttendRequestData {
  meetingId: number;
  unit: string;
  name: string;
  gender: string;
  phone: string;
  email: string;
  trainNumber: string;
  arrivalTime: string;
}

// 定义路由参数接口
interface RouterParams {
  meetingId?: number;
  meetingTitle?: string;
}

@Entry
@Component
struct MeetingDetailPage {
  @State meetingDetail: MeetingDetail | null = null;
  @State isLoading: boolean = true;
  @State errorMessage: string = '';
  private meetingId: number = 0;

  aboutToAppear() {
    // 使用明确的类型代替 any
    const params: RouterParams = router.getParams() as RouterParams;
    if (params && params.meetingId) {
      this.meetingId = params.meetingId;
      this.loadMeetingDetail();
    } else {
      this.errorMessage = '会议ID无效';
      this.isLoading = false;
    }
  }

  private async loadMeetingDetail() {
    this.isLoading = true;
    this.errorMessage = '';

    try {
      let httpRequest = http.createHttp();
      let url = `http://localhost:8080/api/meeting/detail/${this.meetingId}`;

      let response = await httpRequest.request(
        url,
        { method: http.RequestMethod.GET }
      );

      if (response.responseCode === http.ResponseCode.OK) {
        const result: ApiResponse = JSON.parse(response.result as string);
        if (result.code === 200) {
          this.meetingDetail = result.data;
        } else {
          this.errorMessage = result.message || '获取会议详情失败';
        }
      } else {
        this.errorMessage = `服务器错误: ${response.responseCode}`;
      }
    } catch (error) {
      this.errorMessage = '网络异常，请重试';
    } finally {
      this.isLoading = false;
    }
  }

  // 优化时间格式化函数
  private formatDateTime(dateTime: string): string {
    if (!dateTime) return '';

    // 直接处理 YYYY-MM-DD HH:mm:ss 格式
    return dateTime.substring(0, 16); // 显示为 "YYYY-MM-DD HH:mm"
  }

  private getStatusText(status: number): string {
    switch (status) {
      case 0: return '未开始';
      case 1: return '已结束';
      case 2: return '进行中';
      case 3: return '已取消';
      default: return '未知';
    }
  }

  private getStatusColor(status: number): ResourceColor {
    switch (status) {
      case 0: return $r('app.color.status_not_started');
      case 1: return $r('app.color.status_in_progress');
      case 2: return $r('app.color.status_ended');
      case 3: return $r('app.color.status_cancelled');
      default: return $r('app.color.status_unknown');
    }
  }

  // 跳转到填写参会信息页面
  // 跳转到填写参会信息页面
  private navigateToFillInfo() {
    if (this.meetingDetail) {
      // 定义明确类型的路由参数
      const params: RouterParams = {
        meetingId: this.meetingId,
        meetingTitle: this.meetingDetail.title
      };

      // 使用正确的路由路径
      router.pushUrl({
        url: 'pages/FillInfoPage', // 确保路径正确
        params: params
      }, router.RouterMode.Single, (err) => {
        if (err) {
          console.error(`Failed to navigate: ${JSON.stringify(err)}`);
        }
      });
    }
  }

  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Button() {
          Image($r('app.media.back'))
            .width(24)
            .height(24)
        }
        .type(ButtonType.Circle)
        .width(40)
        .height(40)
        .backgroundColor($r('app.color.button_background'))
        .onClick(() => router.back())

        Text('会议详情')
          .fontSize($r('app.float.title_font_size'))
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
          .margin({ left: 10 })
          .layoutWeight(1)
      }
      .width('100%')
      .padding(16)
      .alignItems(VerticalAlign.Center)

      // 加载状态处理
      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .color($r('app.color.primary'))
            .width(60)
            .height(60)
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else if (this.errorMessage) {
        Column() {
          Image($r('app.media.ic_error'))
            .width(80)
            .height(80)
            .margin({ bottom: 20 })
          Text(this.errorMessage)
            .fontSize(18)
            .fontColor($r('app.color.error'))
            .margin({ bottom: 20 })
          Button('重新加载')
            .type(ButtonType.Capsule)
            .width(150)
            .height(40)
            .backgroundColor($r('app.color.primary'))
            .fontColor($r('app.color.white'))
            .onClick(() => this.loadMeetingDetail())
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else if (this.meetingDetail) {
        Scroll() {
          Column() {
            // 会议封面
            if (this.meetingDetail.cover) {
              Image(this.meetingDetail.cover)
                .width('100%')
                .height(200)
                .objectFit(ImageFit.Cover)
            } else {
              Image($r('app.media.meeting_default'))
                .width('100%')
                .height(200)
                .objectFit(ImageFit.Cover)
            }

            // 会议基本信息卡片
            Column({ space: 16 }) {
              // 标题和状态行
              Row() {
                Text(this.meetingDetail.title)
                  .fontSize($r('app.float.meeting_title_font_size'))
                  .fontWeight(FontWeight.Bold)
                  .fontColor($r('app.color.text_primary'))
                  .maxLines(2)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .layoutWeight(1)

                Text(this.getStatusText(this.meetingDetail.status))
                  .fontSize($r('app.float.status_font_size'))
                  .fontColor($r('app.color.white'))
                  .padding({ left: 12, right: 12, top: 6, bottom: 6 })
                  .backgroundColor(this.getStatusColor(this.meetingDetail.status))
                  .borderRadius(12)
              }
              .width('100%')

              // 开始时间卡片
              Row({ space: 12 }) {
                Image($r('app.media.ic_time'))
                  .width(28)
                  .height(28)

                Column({ space: 6 }) {
                  Text('开始时间')
                    .fontSize($r('app.float.card_label_font_size'))
                    .fontColor($r('app.color.text_secondary'))
                  Text(this.formatDateTime(this.meetingDetail.start_time))
                    .fontSize($r('app.float.card_value_font_size'))
                    .fontColor($r('app.color.text_primary'))
                }
                .layoutWeight(1)
              }
              .width('100%')
              .padding(16)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius(12)

              // 结束时间卡片
              Row({ space: 12 }) {
                Image($r('app.media.ic_time'))
                  .width(28)
                  .height(28)

                Column({ space: 6 }) {
                  Text('结束时间')
                    .fontSize($r('app.float.card_label_font_size'))
                    .fontColor($r('app.color.text_secondary'))
                  Text(this.formatDateTime(this.meetingDetail.end_time))
                    .fontSize($r('app.float.card_value_font_size'))
                    .fontColor($r('app.color.text_primary'))
                }
                .layoutWeight(1)
              }
              .width('100%')
              .padding(16)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius(12)

              // 地点信息卡片
              Row({ space: 12 }) {
                Image($r('app.media.ic_location'))
                  .width(28)
                  .height(28)

                Column({ space: 6 }) {
                  Text('会议地点')
                    .fontSize($r('app.float.card_label_font_size'))
                    .fontColor($r('app.color.text_secondary'))
                  Text(this.meetingDetail.location)
                    .fontSize($r('app.float.card_value_font_size'))
                    .fontColor($r('app.color.text_primary'))
                }
                .layoutWeight(1)
              }
              .width('100%')
              .padding(16)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius(12)

              // 会议描述卡片
              Column({ space: 10 }) {
                Text('会议描述')
                  .fontSize($r('app.float.description_title_font_size'))
                  .fontWeight(FontWeight.Medium)
                  .fontColor($r('app.color.text_primary'))
                Text(this.meetingDetail.content || '暂无描述')
                  .fontSize($r('app.float.description_content_font_size'))
                  .fontColor($r('app.color.text_secondary'))
                  .lineHeight(24)
              }
              .width('100%')
              .padding(16)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius(12)
              .margin({ top: 8 })
            }
            .padding(16)
          }
        }
        .layoutWeight(1)

        // 参加会议按钮 - 现在跳转到填写信息页面
        Button(this.meetingDetail.is_attending ? '已参加' : '参加会议', {
          type: ButtonType.Capsule
        })
          .width('90%')
          .height(50)
          .backgroundColor(
            this.meetingDetail.is_attending
              ? $r('app.color.button_disabled')
              : $r('app.color.primary')
          )
          .fontColor($r('app.color.white'))
          .fontSize($r('app.float.button_font_size'))
          .enabled(!this.meetingDetail.is_attending)
          .margin({ bottom: 20, top: 10 })
          .onClick(() => this.navigateToFillInfo())
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }
}

// 参会信息填写页面
@Component
export struct FillInfoPage {
  @State unit: string = '';          // 单位
  @State name: string = '';          // 姓名
  @State gender: string = '男';      // 性别，默认男
  @State phone: string = '';         // 手机号码
  @State email: string = '';         // 电子邮箱
  @State trainNumber: string = '';   // 到达车次
  @State arrivalTime: string = '';   // 到达时间

  @State isLoading: boolean = false;
  @State errorMessage: string = '';

  private meetingId: number = 0;
  private meetingTitle: string = '';

  aboutToAppear() {
    // 使用明确的类型代替 any
    const params: RouterParams = router.getParams() as RouterParams;
    if (params) {
      this.meetingId = params.meetingId || 0;
      this.meetingTitle = params.meetingTitle || '会议';
    }
  }

  // 提交参会信息
  private async submitInfo() {
    // 表单验证
    if (!this.name.trim()) {
      this.errorMessage = '请填写姓名';
      return;
    }

    if (!this.phone.trim() || !/^1[3-9]\d{9}$/.test(this.phone)) {
      this.errorMessage = '请填写有效的手机号码';
      return;
    }

    if (!this.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      this.errorMessage = '请填写有效的电子邮箱';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    try {
      let httpRequest = http.createHttp();
      let url = 'http://localhost:8080/api/meeting/attend';

      // 使用明确的请求数据类型
      const requestData: AttendRequestData = {
        meetingId: this.meetingId,
        unit: this.unit,
        name: this.name,
        gender: this.gender,
        phone: this.phone,
        email: this.email,
        trainNumber: this.trainNumber,
        arrivalTime: this.arrivalTime
      };

      let response = await httpRequest.request(
        url,
        {
          method: http.RequestMethod.POST,
          header: {  // 修复拼写错误：headers -> header
            'Content-Type': 'application/json'
          },
          extraData: JSON.stringify(requestData)
        }
      );

      if (response.responseCode === http.ResponseCode.OK) {
        const result: AttendResponse = JSON.parse(response.result as string);
        if (result.code === 200) {
          // 提交成功，返回会议详情页
          router.back();
        } else {
          this.errorMessage = result.message || '提交失败，请重试';
        }
      } else {
        this.errorMessage = `服务器错误: ${response.responseCode}`;
      }
    } catch (error) {
      this.errorMessage = '网络异常，请重试';
    } finally {
      this.isLoading = false;
    }
  }

  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Button() {
          Image($r('app.media.back'))
            .width(24)
            .height(24)
        }
        .type(ButtonType.Circle)
        .width(40)
        .height(40)
        .backgroundColor($r('app.color.button_background'))
        .onClick(() => router.back())

        Text('填写参会信息')
          .fontSize($r('app.float.title_font_size'))
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
          .margin({ left: 10 })
          .layoutWeight(1)
      }
      .width('100%')
      .padding(16)
      .alignItems(VerticalAlign.Center)

      // 会议标题
      Text(this.meetingTitle)
        .fontSize($r('app.float.subtitle_font_size'))
        .fontWeight(FontWeight.Medium)
        .fontColor($r('app.color.primary'))
        .width('90%')
        .margin({ top: 10, bottom: 20 })
        .textAlign(TextAlign.Center)

      Scroll() {
        Column({ space: 20 }) {
          // 单位
          Column() {
            Text('单位')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '请输入单位名称' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.unit = value)
          }

          // 姓名
          Column() {
            Text('姓名')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '请输入姓名' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.name = value)
          }

          // 性别
          Column() {
            Text('性别')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            Row({ space: 20 }) {
              Button('男')
                .width('45%')
                .height(50)
                .fontSize($r('app.float.input_font_size'))
                .backgroundColor(this.gender === '男' ? $r('app.color.primary') : $r('app.color.button_background'))
                .fontColor(this.gender === '男' ? $r('app.color.white') : $r('app.color.text_primary'))
                .onClick(() => this.gender = '男')

              Button('女')
                .width('45%')
                .height(50)
                .fontSize($r('app.float.input_font_size'))
                .backgroundColor(this.gender === '女' ? $r('app.color.primary') : $r('app.color.button_background'))
                .fontColor(this.gender === '女' ? $r('app.color.white') : $r('app.color.text_primary'))
                .onClick(() => this.gender = '女')
            }
          }

          // 手机号码
          Column() {
            Text('手机号码')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '请输入手机号码' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .type(InputType.Number)
              .maxLength(11)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.phone = value)
          }

          // 电子邮箱
          Column() {
            Text('电子邮箱')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '请输入电子邮箱' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.email = value)
          }

          // 到达车次
          Column() {
            Text('到达车次')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '请输入车次/航班号' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.trainNumber = value)
          }

          // 到达时间
          Column() {
            Text('到达时间')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })
            TextInput({ placeholder: '格式：YYYY-MM-DD HH:mm' })
              .width('100%')
              .height(50)
              .fontSize($r('app.float.input_font_size'))
              .padding(10)
              .borderRadius(8)
              .border({ width: 1, color: $r('app.color.border') })
              .onChange(value => this.arrivalTime = value)
          }
        }
        .padding(20)
      }
      .layoutWeight(1)

      // 错误提示
      if (this.errorMessage) {
        Text(this.errorMessage)
          .fontSize($r('app.float.error_font_size'))
          .fontColor($r('app.color.error'))
          .margin({ bottom: 10 })
      }

      // 提交按钮
      Button('提交', { type: ButtonType.Capsule })
        .width('90%')
        .height(50)
        .backgroundColor($r('app.color.primary'))
        .fontColor($r('app.color.white'))
        .fontSize($r('app.float.button_font_size'))
        .margin({ bottom: 20, top: 10 })
        .onClick(() => this.submitInfo())
        .enabled(!this.isLoading)

      // 加载指示器
      if (this.isLoading) {
        LoadingProgress()
          .color($r('app.color.primary'))
          .width(50)
          .height(50)
          .margin({ bottom: 20 })
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }
}