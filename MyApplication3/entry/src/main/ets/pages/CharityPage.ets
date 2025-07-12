// pages/CharityPage.ets
import router from '@ohos.router';

@Entry
@Component
struct CharityPage {
  private pageTitle: string = "公益行动";

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
          Text('公益行动')
            .fontSize(22)
            .fontWeight(FontWeight.Bold)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 16 })
            .width('100%')

          Text('会员单位积极参与欠发达地区留守儿童公益活动，用技术力量回馈社会。')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('主要项目：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. "科技进校园"活动：为偏远地区学校捐赠电脑设备，开展编程启蒙课程\n' +
            '2. "爱心助学"计划：资助贫困学生完成学业\n' +
            '3. "数字素养提升"项目：为留守儿童提供计算机基础知识培训\n' +
            '4. "远程教育支持"：建立线上教育平台，提供优质教育资源')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('近期活动：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('• 2023年9月：云南昌宁计委会捐赠活动\n' +
            '• 2023年11月：贵州山区学校电脑教室建设\n' +
            '• 2023年12月：四川凉山州留守儿童冬衣捐赠')
            .fontSize(16)
            .fontColor($r('app.color.primary_text'))
            .lineHeight(24)
            .margin({ bottom: 20 })

          Text('参与方式：')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor($r('app.color.primary_text'))
            .margin({ bottom: 10 })

          Text('1. 物资捐赠：电脑、图书、学习用品等\n' +
            '2. 资金支持：通过协会公益基金捐赠\n' +
            '3. 志愿者服务：参与支教、培训等活动\n' +
            '4. 技术支持：为公益项目提供技术解决方案')
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