// MeetingList.ets
import { router } from '@kit.ArkUI';
import { BusinessError } from '@ohos.base';
import { http } from '@kit.NetworkKit';
import promptAction from '@ohos.promptAction';

// 会议数据模型
interface Meeting {
  id: number;
  title: string;
  start_time: string;
  end_time: string;
  cover: string;
  status: number;
  location: string;
  participants: number;
  description: string;
}

// API响应接口
interface ApiResponse {
  code: number;
  message: string;
  data: Meeting[];
}

@Entry
@Component
struct MeetingListPage {
  @State meetingData: Meeting[] = [];
  @State isLoading: boolean = true;
  @State errorMessage: string = '';
  @State searchText: string = '';

  aboutToAppear() {
    this.loadMeetingData();
  }

  private async loadMeetingData() {
    this.isLoading = true;
    this.errorMessage = '';

    try {
      let httpRequest = http.createHttp();
      let url = 'http://localhost:8080/api/meeting/list';

      let response = await httpRequest.request(
        url,
        { method: http.RequestMethod.GET }
      );

      if (response.responseCode === http.ResponseCode.OK) {
        const result: ApiResponse = JSON.parse(response.result as string);
        if (result.code === 200) {
          this.meetingData = result.data;
        } else {
          this.errorMessage = result.message || '获取数据失败';
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

  private formatDateTime(dateTime: string): string {
    if (!dateTime) return '';

    const parts = dateTime.split('T');
    if (parts.length < 2) return dateTime;

    const datePart = parts[0];
    const timePart = parts[1].substring(0, 5);

    return `${datePart} ${timePart}`;
  }

  private getStatusText(status: number): string {
    switch (status) {
      case 0: return '未开始';
      case 1: return '审核中';
      case 2: return '进行中';
      case 3: return '已取消';
      default: return '未知';
    }
  }

  private getStatusColor(status: number): ResourceColor {
    switch (status) {
      case 0: return '#3498db';  // 蓝色
      case 1: return '#e74c3c';  // 红色
      case 2: return '#2ecc71';  // 绿色 - 进行中的会议
      case 3: return '#95a5a6';  // 灰色
      default: return '#9b59b6'; // 紫色
    }
  }

  // 获取完整图片路径
  private getFullImagePath(path: string): string {

    return `http://localhost:8080/${path}`;
  }

  // 处理会议点击事件
  private handleMeetingClick(meeting: Meeting) {
    if (meeting.status === 2) {
      router.pushUrl({
        url: 'pages/MeetingDetailPage',
        params: { meetingId: meeting.id }
      });
    } else {
      promptAction.showToast({
        message: `会议"${meeting.title}"当前状态为${this.getStatusText(meeting.status)}，无法进入`,
        duration: 2000
      });
    }
  }

  build() {
    Column() {
      // 顶部标题栏
      Row() {
        // 返回按钮
        Button() {
          Image($r('app.media.back'))
            .width(24)
            .height(24)
        }
        .type(ButtonType.Circle)
        .width(40)
        .height(40)
        .backgroundColor('#e6f0ff')
        .margin({ right: 12 })
        .onClick(() => {
          router.back();
        })

        Text('会议列表')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .fontColor('#1a73e8')
          .layoutWeight(1)
      }
      .width('100%')
      .padding(16)
      .alignItems(VerticalAlign.Center)
      .backgroundColor(Color.White)
      .shadow({ radius: 4, color: '#1a000000', offsetX: 0, offsetY: 2 })

      // 搜索栏
      Row() {
        TextInput({ placeholder: '搜索会议...' })
          .layoutWeight(1)
          .height(40)
          .backgroundColor(Color.White)
          .borderRadius(20)
          .padding({ left: 15, right: 15 })
          .onChange((value: string) => {
            this.searchText = value;
          })
      }
      .width('90%')
      .padding(10)
      .margin({ top: 10 })

      // 内容区域
      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .color('#1a73e8')
            .width(60)
            .height(60)
          Text('加载会议数据中...')
            .margin({ top: 20 })
            .fontColor('#666666')
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else if (this.errorMessage) {
        Column() {
          Image($r('app.media.foreground'))
            .width(80)
            .height(80)
            .margin({ bottom: 20 })
          Text(this.errorMessage)
            .fontSize(18)
            .fontColor('#e74c3c')
            .margin({ bottom: 20 })
          Button('重新加载')
            .type(ButtonType.Capsule)
            .width(150)
            .height(40)
            .backgroundColor('#1a73e8')
            .fontColor(Color.White)
            .onClick(() => this.loadMeetingData())
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else if (this.meetingData.length === 0) {
        Column() {
          Image($r('app.media.foreground'))
            .width(120)
            .height(120)
            .margin({ bottom: 20 })
          Text('暂无会议数据')
            .fontSize(20)
            .fontColor('#95a5a6')
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else {
        // 会议列表
        List({ space: 12 }) {
          ForEach(this.meetingData, (meeting: Meeting) => {
            ListItem() {
              this.MeetingItem(meeting)
            }
            .onClick(() => this.handleMeetingClick(meeting))
          })
        }
        .width('100%')
        .height('100%')
        .layoutWeight(1)
        .divider({
          strokeWidth: 1,
          color: '#f0f0f0',
          startMargin: 90,
          endMargin: 16
        })
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f8f9fa')
  }

  @Builder
  MeetingItem(meeting: Meeting) {
    Row({ space: 16 }) {
      // 左侧图片 - 使用完整路径
      if (meeting.cover) {
        Image(this.getFullImagePath(meeting.cover))
          .width(70)
          .height(70)
          .borderRadius(8)
          .objectFit(ImageFit.Cover)
      } else {
        Image($r('app.media.meeting_default'))
          .width(70)
          .height(70)
          .borderRadius(8)
          .objectFit(ImageFit.Cover)
      }

      // 右侧内容区域
      Column() {
        // 标题居中
        Text(meeting.title)
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#333333')
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .width('100%')
          .textAlign(TextAlign.Center)

        // 时间信息分为两行显示
        Column({ space: 4 }) {
          // 开始时间
          Row() {
            Image($r('app.media.ic_time'))
              .width(16)
              .height(16)
              .margin({ right: 6 })
            Text(`开始: ${this.formatDateTime(meeting.start_time)}`)
              .fontSize(14)
              .fontColor('#666666')
          }

          // 结束时间
          Row() {
            Image($r('app.media.ic_time'))
              .width(16)
              .height(16)
              .margin({ right: 6 })
            Text(`结束: ${this.formatDateTime(meeting.end_time)}`)
              .fontSize(14)
              .fontColor('#666666')
          }
        }
        .margin({ top: 8, bottom: 8 })
        .width('100%')

        // 底部行（地点+状态）
        Row() {
          // 地点信息
          Row() {
            Image($r('app.media.ic_location'))
              .width(16)
              .height(16)
              .margin({ right: 6 })
            Text(meeting.location)
              .fontSize(14)
              .fontColor('#666666')
              .maxLines(1)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
          }
          .layoutWeight(1)

          // 状态标签（右下角）
          Text(this.getStatusText(meeting.status))
            .fontSize(12)
            .fontColor(Color.White)
            .padding({ left: 8, right: 8, top: 2, bottom: 2 })
            .backgroundColor(this.getStatusColor(meeting.status))
            .borderRadius(10)
        }
        .width('100%')
      }
      .layoutWeight(1)
    }
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .width('100%')

    // 添加半透明遮罩层（如果会议不可点击）
    .opacity(meeting.status === 2 ? 1 : 0.7)
  }
}