// pages/StandardPage.ets
import router from '@ohos.router';

@Entry
@Component
struct StandardPage {
  private pageTitle: string = "标准制定";

  onPageShow() {
    const params = router.getParams() as Record<string, string>;
    if (params && params.title) {
      this.pageTitle = params.title;
    }
  }

  // 返回上一页
  private goBack() {
    router.back();
  }

  build() {
    Column() {
      // 顶部导航栏
      Row({ space: 10 }) {
        // 返回按钮
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

        // 页面标题
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
          Text('软件测试行业标准制定')
            .fontSize(22)
            .fontWeight(FontWeight.Bold)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 16 })
            .width('100%')

          Text('协会组织成员单位联合制定软件测试行业标准，旨在推动行业规范化发展，提升软件产品质量。')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('主要工作内容：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. 制定软件测试基础标准，包括测试术语、测试流程、测试文档规范等\n' +
            '2. 制定各领域软件测试专项标准，如金融、医疗、工业控制等领域\n' +
            '3. 制定自动化测试工具评估标准\n' +
            '4. 制定测试人员能力评估标准\n' +
            '5. 制定软件质量评估体系')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('参与单位：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• 中国软件评测中心\n' +
            '• 国家信息中心软件评测中心\n' +
            '• 中国电子技术标准化研究院\n' +
            '• 各大软件企业测试部门')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('标准制定流程：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. 需求调研与分析\n' +
            '2. 草案编写\n' +
            '3. 专家评审\n' +
            '4. 公开征求意见\n' +
            '5. 修改完善\n' +
            '6. 最终评审\n' +
            '7. 发布实施')
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