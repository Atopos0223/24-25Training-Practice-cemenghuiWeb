// pages/ToolsPage.ets
import router from '@ohos.router';

@Entry
@Component
struct ToolsPage {
  private pageTitle: string = "工具研发";

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
          Text('软件测试工具研发')
            .fontSize(22)
            .fontWeight(FontWeight.Bold)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 16 })
            .width('100%')

          Text('协会组织会员单位共同研发软件测试相关工具，推动测试技术创新和工具国产化。')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('主要研发方向：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. 自动化测试工具\n' +
            '2. 性能测试工具\n' +
            '3. 安全测试工具\n' +
            '4. 测试管理平台\n' +
            '5. 测试数据生成工具\n' +
            '6. 测试环境管理工具')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('研发成果：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• TestMaster 自动化测试平台\n' +
            '• PerfExpert 性能测试工具\n' +
            '• SecScan 安全扫描工具\n' +
            '• DataGen 测试数据生成工具\n' +
            '• EnvManager 测试环境管理平台')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('参与单位：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• 中软国际\n' +
            '• 用友网络\n' +
            '• 金蝶软件\n' +
            '• 东软集团\n' +
            '• 华为技术有限公司')
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