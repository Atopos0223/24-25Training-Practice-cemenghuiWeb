// FillInfoPage.ets
import { router } from '@kit.ArkUI';
import { http } from '@kit.NetworkKit';

// 定义路由参数接口
interface RouterParams {
  meetingId?: number;
  meetingTitle?: string;
}

// 参会请求数据接口
interface AttendRequestData {
  meetingId: number;
  unit: string;
  name: string;
  gender: string;
  phone: string;
  email: string;
  arrivalTime: string;
}

// 参会响应接口
interface AttendResponse {
  code: number;
  message: string;
}

@Entry
@Component
struct FillInfoPage {
  @State unit: string = '';          // 单位
  @State name: string = '';          // 姓名
  @State gender: string = '男';      // 性别，默认男
  @State phone: string = '';         // 手机号码
  @State email: string = '';         // 电子邮箱
  @State arrivalTime: string = '';   // 到达时间
  @State isLoading: boolean = false;
  @State errorMessage: string = '';
  @State isMilitaryTime: boolean = true; // 使用24小时制

  // 控制日期时间选择器显示状态
  @State showPicker: boolean = false;
  // 临时存储选择的日期
  @State selectedDate: Date = new Date();
  // 临时存储选择的时间
  @State selectedTime: Date = new Date();

  private meetingId: number = 0;
  private meetingTitle: string = '';

  aboutToAppear() {
    // 获取路由参数
    const params: RouterParams = router.getParams() as RouterParams;
    if (params) {
      this.meetingId = params.meetingId || 0;
      this.meetingTitle = params.meetingTitle || '会议';
    }
  }

  // 打开日期时间选择器
  private openDateTimePicker() {
    if (this.arrivalTime) {
      const date = new Date(this.arrivalTime);
      this.selectedDate = date;
      this.selectedTime = date;
    } else {
      const now = new Date();
      this.selectedDate = now;
      this.selectedTime = now;
    }
    this.showPicker = true;
  }

  // 确认日期时间选择
  private confirmDateTime() {
    // 组合日期和时间
    const combinedDate = new Date(
      this.selectedDate.getFullYear(),
      this.selectedDate.getMonth(),
      this.selectedDate.getDate(),
      this.selectedTime.getHours(),
      this.selectedTime.getMinutes()
    );
    this.arrivalTime = this.formatDateTime(combinedDate);
    this.showPicker = false;
  }

  // 格式化日期时间为ISO 8601格式
  private formatDateTime(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');

    // 获取时区偏移量 (分钟)
    const timezoneOffset = date.getTimezoneOffset();
    const offsetHours = Math.abs(Math.floor(timezoneOffset / 60)).toString().padStart(2, '0');
    const offsetMinutes = Math.abs(timezoneOffset % 60).toString().padStart(2, '0');
    const offsetSign = timezoneOffset > 0 ? '-' : '+';

    // ISO 8601格式: YYYY-MM-DDTHH:mm:ss.SSS±HH:mm
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000${offsetSign}${offsetHours}:${offsetMinutes}`;
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

    // 验证到达时间
    if (!this.arrivalTime) {
      this.errorMessage = '请选择到达时间';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    try {
      let httpRequest = http.createHttp();
      let url = 'http://localhost:8080/api/meeting-attend/add';

      // 构建请求数据
      const requestData: AttendRequestData = {
        meetingId: this.meetingId,
        unit: this.unit,
        name: this.name,
        gender: this.gender,
        phone: this.phone,
        email: this.email,
        arrivalTime: this.arrivalTime
      };

      let response = await httpRequest.request(
        url,
        {
          method: http.RequestMethod.POST,
          header: {
            'Content-Type': 'application/json'
          },
          extraData: JSON.stringify(requestData)
        }
      );

      if (response.responseCode === http.ResponseCode.OK) {
        const result: AttendResponse = JSON.parse(response.result as string);
        if (result.code === 200) {
          // 提交成功，返回会议详情页
          this.errorMessage = 'chenggong'
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

          // 到达时间
          Column() {
            Text('到达时间')
              .fontSize($r('app.float.input_label_font_size'))
              .fontColor($r('app.color.text_primary'))
              .margin({ bottom: 8 })

            // 简洁的时间选择按钮
            Button(this.formatDisplayTime() || '选择到达时间', { type: ButtonType.Normal }) {
              Row() {
                Text(this.formatDisplayTime() || '选择到达时间')
                  .fontSize($r('app.float.input_font_size'))
                  .fontColor(this.arrivalTime ? $r('app.color.text_primary') : $r('app.color.text_secondary'))
                  .layoutWeight(1)
                Image($r('app.media.ic_time')) // 使用时间图标
                  .width(20)
                  .height(20)
                  .margin({ left: 8 })
              }
              .width('100%')
              .padding({ left: 12, right: 12 })
            }
            .width('100%')
            .height(50)
            .backgroundColor($r('app.color.button_background'))
            .onClick(() => this.openDateTimePicker())
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
          .textAlign(TextAlign.Center)
          .width('100%')
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

      // 日期时间选择器弹窗
      if (this.showPicker) {
        // 半透明背景层
        Blank()
          .width('100%')
          .height('100%')
          .backgroundColor(0x33000000)
          .onClick(() => this.showPicker = false)
          .position({ x: 0, y: 0 })
          .zIndex(99)

        // 日期时间选择器面板
        Column() {
          // 标题栏
          Row() {
            Button('取消')
              .fontSize($r('app.float.button_font_size'))
              .fontColor($r('app.color.text_secondary'))
              .onClick(() => this.showPicker = false)

            Text('选择到达时间')
              .fontSize($r('app.float.title_font_size'))
              .fontWeight(FontWeight.Bold)
              .layoutWeight(1)
              .textAlign(TextAlign.Center)

            Button('确定')
              .fontSize($r('app.float.button_font_size'))
              .fontColor($r('app.color.primary'))
              .onClick(() => this.confirmDateTime())
          }
          .width('100%')
          .padding(16)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius({ topLeft: 12, topRight: 12 })

          // 日期选择器 - 优化版本
          DatePicker({
            start: new Date(2020, 0, 1),
            end: new Date(2030, 11, 31),
            selected: this.selectedDate
          })
            .onDateChange((value: Date) => {
              // 直接获取完整的Date对象
              this.selectedDate = value;
            })
            .width('100%')
            .height(200)
            .backgroundColor($r('app.color.background'))

          // 时间选择器
          TimePicker({
            selected: this.selectedTime
          })
            .useMilitaryTime(this.isMilitaryTime)
            .onChange((value: TimePickerResult) => {
              if (value.hour >= 0) {
                // 创建新Date对象确保状态变化被检测
                const newTime = new Date(this.selectedTime);
                newTime.setHours(value.hour, value.minute);
                this.selectedTime = newTime;
              }
            })
            .width('100%')
            .height(150)
            .backgroundColor($r('app.color.background'))
        }
        .width('90%')
        .borderRadius(12)
        .backgroundColor($r('app.color.card_background'))
        .position({ x: '5%', y: '20%' })
        .zIndex(100)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  // 格式化显示时间 (用于UI展示)
  private formatDisplayTime(): string {
    if (!this.arrivalTime) return '';

    try {
      const date = new Date(this.arrivalTime);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');

      return `${year}-${month}-${day} ${hours}:${minutes}`;
    } catch (e) {
      return this.arrivalTime;
    }
  }
}