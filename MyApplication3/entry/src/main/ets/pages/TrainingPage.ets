// pages/TrainingPage.ets
import router from '@ohos.router';

@Entry
@Component
struct TrainingPage {
  private pageTitle: string = "技术培训";

  onPageShow() {
    const params = router.getParams() as Record<string, string>;
    if (params && params.title) {
      this.pageTitle = params.title;
    }
  }

  private goBack() {
    router.back();
  }

  build() {
    Column() {
      // 顶部导航栏（同上）
      Row({ space: 10 }) {
        Button() {
          Image($r('app.media.back'))
            .width(24)
            .height(24)
        }
        .type(ButtonType.Normal)
        .width(40)
        .height(40)
        .borderRadius(20)
        .backgroundColor($r('app.color.button_bg'))
        .onClick(() => this.goBack())

        Text(this.pageTitle)
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.primary_text'))
          .margin({ left: 10 })
          .layoutWeight(1)
      }
      .width('100%')
      .padding(16)
      .alignItems(VerticalAlign.Center)
      .backgroundColor(Color.White)
      .shadow({
        radius: 4,
        color: $r('app.color.shadow'),
        offsetX: 0,
        offsetY: 2
      })

      // 内容区域
      Scroll() {
        Column() {
          Text('软件测试技术培训')
            .fontSize(22)
            .fontWeight(FontWeight.Bold)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 16 })
            .width('100%')

          Text('协会组织会员单位开展软件测试技术、软件造价评估等专业培训，旨在提升行业人才技术水平。')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('培训课程：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• 软件测试基础\n' +
            '• 自动化测试技术\n' +
            '• 性能测试与调优\n' +
            '• 安全测试技术\n' +
            '• 移动应用测试\n' +
            '• AI测试技术\n' +
            '• 软件造价评估\n' +
            '• 测试管理')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('培训形式：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. 线下集中培训（3-5天）\n' +
            '2. 线上直播课程\n' +
            '3. 企业定制内训\n' +
            '4. 工作坊与实践操作')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('认证体系：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• 软件测试工程师认证\n' +
            '• 自动化测试专家认证\n' +
            '• 性能测试专家认证\n' +
            '• 安全测试专家认证\n' +
            '• 软件造价评估师认证')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
        }
        .width('100%')
        .padding(20)
      }
      .scrollBar(BarState.Off)
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }
}