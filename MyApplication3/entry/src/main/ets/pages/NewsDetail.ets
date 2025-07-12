// NewsDetail.ets
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';

@Entry
@Component
struct NewsDetailPage {
  @State private title: string = '加载中...';
  @State private content: string = '正在加载新闻内容，请稍候...';

  // 页面加载时获取参数
  onPageShow() {
    const params = router.getParams() as Record<string, string>;
    if (params) {
      this.title = params.title || '无标题';
      this.content = params.content || '无内容';
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
        .backgroundColor('#f0f0f0')
        .onClick(() => this.goBack())

        // 页面标题
        Text(this.title.length > 15 ?
          this.title.substring(0, 15) + '...' : this.title)
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor('#333')
          .margin({ left: 10 })
          .layoutWeight(1)
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#ffffff')
      .shadow({ radius: 4, color: '#00000020', offsetX: 0, offsetY: 2 })

      // 新闻内容区域
      Scroll() {
        Column() {
          // 新闻标题
          Text(this.title)
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
            .fontColor('#222')
            .margin({ bottom: 16, top: 20 })
            .width('100%')
            .textAlign(TextAlign.Start)
            .padding({ left: 20, right: 20 })

          // 分割线
          Divider()
            .strokeWidth(1)
            .color('#e0e0e0')
            .margin({ bottom: 20 })
            .padding({ left: 20, right: 20 })

          // 新闻内容
          Text(this.content)
            .fontSize(18)
            .fontColor('#444')
            .lineHeight(28)
            .width('100%')
            .textAlign(TextAlign.Start)
            .padding({ left: 20, right: 20, bottom: 40 })
        }
        .width('100%')
      }
      .layoutWeight(1)
      .scrollBar(BarState.Off)

      // 底部操作栏
      Row() {
        Button('分享')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .width(100)
          .height(40)
          .backgroundColor('#4285f4')
          .fontColor(Color.White)
          .onClick(() => {
            promptAction.showToast({ message: "分享功能开发中", duration: 2000 });
          })

        Button('收藏')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .width(100)
          .height(40)
          .margin({ left: 10 })
          .backgroundColor('#f0f0f0')
          .fontColor('#333')
          .onClick(() => {
            promptAction.showToast({ message: "收藏功能开发中", duration: 2000 });
          })
      }
      .width('100%')
      .padding(16)
      .justifyContent(FlexAlign.Center)
      .backgroundColor('#ffffff')
      .shadow({ radius: 4, color: '#00000020', offsetX: 0, offsetY: -2 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f8f8f8')
  }
}