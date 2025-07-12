// MainPage.ets
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import { http } from '@kit.NetworkKit';
import { BusinessError } from '@ohos.base';

// 根据数据库表结构定义行业动态数据结构
interface IndustryNews {
  id: number;           // 对应数据库id
  author_id: number;    // 对应数据库author_id
  title: string;        // 对应数据库title
  image?: string;       // 对应数据库image（可选）
  summary?: string;     // 对应数据库summary（可选）
  content: string;      // 对应数据库content
  status: number;       // 对应数据库status
  create_time: string;  // 对应数据库create_time
  update_time?: string; // 对应数据库update_time（可选）
}

// API响应接口
interface ApiResponse {
  code: number;
  message: string;
  data: IndustryNews[];
}

// 合作项目数据结构
interface CooperationItem {
  id: number;
  title: string;
  description: string;
  date?: string;
  location?: string;
}

@Entry
@Component
struct MainPage {
  @State currentTab: number = 0; // 0:动态 1:合作
  private pageTitle: string = "测盟汇移动端";
  @State isLoading: boolean = true; // 加载状态
  @State errorMessage: string = ''; // 错误信息

  // 行业动态数据
  @State industryNews: IndustryNews[] = [];

  // 合作项目数据
  @State cooperationItems: CooperationItem[] = [
    {
      id: 1,
      title: "会议研讨",
      description: "协会组织会员单位召开行业会议，研讨相关技术与行业发展",
    },
    {
      id: 2,
      title: "标准制定",
      description: "协会组织成员单位联合制定软件测试行业标准"
    },
    {
      id: 3,
      title: "技术培训",
      description: "协会组织会员单位开展软件测试技术、软件造价评估等专业培训"
    },
    {
      id: 4,
      title: "工具研发",
      description: "协会组织会员单位共同研发软件测试相关工具"
    },
    {
      id: 5,
      title: "公益行动",
      description: "会员单位积极参与欠发达地区留守儿童公益活动",
      location: "云南故事"
    }
  ];

  aboutToAppear() {
    this.loadNewsData();
  }

  // 日期格式化函数
  private formatDate(dateString: string): string {
    if (!dateString) return '';

    try {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = date.getMonth() + 1; // 月份从0开始
      const day = date.getDate();

      return `${year}年${month}月${day}日`;
    } catch (e) {
      return dateString.split('T')[0]; // 如果解析失败，返回原始日期部分
    }
  }

  // 从API加载新闻数据
  private async loadNewsData() {
    this.isLoading = true;
    this.errorMessage = '';

    try {
      let httpRequest = http.createHttp();
      let url = 'http://localhost:8080/news/all';

      let response = await httpRequest.request(
        url,
        { method: http.RequestMethod.GET }
      );

      if (response.responseCode === http.ResponseCode.OK) {
        const result: ApiResponse = JSON.parse(response.result as string);
        if (result.code === 200) {
          this.industryNews = result.data;
        } else {
          this.errorMessage = result.message || '获取新闻数据失败';
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

  // 退出登录功能
  private logout() {
    promptAction.showDialog({
      title: '退出确认',
      message: '确定要退出登录吗？',
      buttons: [
        { text: '取消', color: '#666666' },
        { text: '确定', color: '#4285f4' }
      ]
    }).then(result => {
      if (result.index === 1) {
        console.log("用户退出登录");
        router.replaceUrl({
          url: 'pages/Index',
          params: { logout: true }
        });
      }
    });
  }

  // 处理合作项目点击事件
  private handleCooperationItemClick(item: CooperationItem) {
    switch (item.id) {
      case 1: // 会议研讨
        this.jumpToMeetingList();
        break;
      case 2: // 标准制定
        router.pushUrl({
          url: 'pages/StandardPage',
          params: { title: item.title }
        });
        break;
      case 3: // 技术培训
        router.pushUrl({
          url: 'pages/TrainingPage',
          params: { title: item.title }
        });
        break;
      case 4: // 工具研发
        router.pushUrl({
          url: 'pages/ToolsPage',
          params: { title: item.title }
        });
        break;
      case 5: // 公益行动
        router.pushUrl({
          url: 'pages/CharityPage',
          params: { title: item.title }
        });
        break;
    }
  }

  // 跳转到会议列表
  private jumpToMeetingList() {
    router.pushUrl({ url: 'pages/MeetingList' });
  }

  // 跳转到详情页
  private jumpToDetail(news: IndustryNews) {
    console.log("跳转到详情页: " + news.id);
    router.pushUrl({
      url: 'pages/NewsDetail',
      params: {
        id: news.id.toString(),
        title: news.title,
        content: news.content,
        image: news.image || "",
        createTime: news.create_time,
        summary: news.summary || "",
        authorId: news.author_id.toString()
      }
    });
  }

  // 渲染行业动态页面
  @Builder
  renderIndustryNews() {
    Column() {
      // 搜索栏
      Row() {
        TextInput({ placeholder: '请输入搜索关键词' })
          .layoutWeight(1)
          .height(45)
          .backgroundColor('#f0f0f0')
          .borderRadius(25)
          .padding({ left: 20, right: 20 })
          .fontSize(16)
      }
      .width('100%')
      .padding(10)
      .margin({ top: 10, bottom: 10 })

      // 内容区域
      if (this.isLoading) {
        Column() {
          LoadingProgress()
            .color('#4285f4')
            .width(60)
            .height(60)
          Text('加载新闻数据中...')
            .margin({ top: 20 })
            .fontColor('#666666')
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
            .fontColor('#ff4444')
            .margin({ bottom: 20 })
          Button('重新加载')
            .type(ButtonType.Capsule)
            .width(150)
            .height(40)
            .backgroundColor('#4285f4')
            .fontColor(Color.White)
            .onClick(() => this.loadNewsData())
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else if (this.industryNews.filter(item => item.status === 1).length === 0) {
        Column() {
          Image($r('app.media.foreground'))
            .width(120)
            .height(120)
            .margin({ bottom: 20 })
          Text('暂无新闻数据')
            .fontSize(20)
            .fontColor('#999999')
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else {
        // 行业动态列表
        Scroll() {
          Column() {
            ForEach(this.industryNews.filter(item => item.status === 1), (news: IndustryNews) => {
              Column() {
                // 标题行
                Row() {
                  Text(news.title)
                    .fontSize(18)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#222222')
                    .maxLines(2)
                    .textOverflow({ overflow: TextOverflow.Ellipsis })
                    .layoutWeight(1)
                }
                .width('100%')
                .margin({ bottom: 10 })

                // 图片和内容预览
                if (news.image) {
                  Image(`http://localhost:8080${news.image}`)
                    .width('100%')
                    .height(200)
                    .objectFit(ImageFit.Cover)
                    .borderRadius(8)
                    .margin({ bottom: 10 })
                }

                // 摘要或内容预览
                if (news.summary) {
                  Text(news.summary)
                    .fontSize(14)
                    .fontColor('#666666')
                    .maxLines(3)
                    .textOverflow({ overflow: TextOverflow.Ellipsis })
                    .margin({ bottom: 10 })
                } else {
                  Text(news.content)
                    .fontSize(14)
                    .fontColor('#666666')
                    .maxLines(3)
                    .textOverflow({ overflow: TextOverflow.Ellipsis })
                    .margin({ bottom: 10 })
                }

                // 修改后的时间显示部分 - 时间在左下角
                Row() {
                  // 创建时间 - 使用新的格式化函数
                  Text(this.formatDate(news.create_time))
                    .fontSize(12)
                    .fontColor('#888888')
                    .layoutWeight(1)
                    .textAlign(TextAlign.Start)

                  // 状态指示器
                  Text(news.status === 1 ? "已发布" : "草稿")
                    .fontSize(12)
                    .fontColor(news.status === 1 ? '#00c853' : '#ff9800')
                }
                .width('100%')
                .margin({ bottom: 5 }) // 减少底部间距

                // 分割线
                Divider()
                  .strokeWidth(1)
                  .color('#e0e0e0')
                  .margin({ top: 15, bottom: 5 })
              }
              .width('100%')
              .padding(16)
              .backgroundColor(Color.White)
              .borderRadius(12)
              .shadow({
                radius: 2,
                color: '#00000010',
                offsetX: 0,
                offsetY: 1
              })
              .onClick(() => this.jumpToDetail(news))
            })
          }
          .width('100%')
          .padding(16)

        }
        .scrollable(ScrollDirection.Vertical)
        .scrollBar(BarState.Off)
        .layoutWeight(1)
      }
    }
  }

  // 渲染合作页面
  @Builder
  renderCooperation() {
    Column() {
      // 顶部标题
      Text('合作')
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
        .fontColor('#222222')
        .margin({ top: 16, bottom: 20 })

      // 合作项目网格
      Grid() {
        ForEach(this.cooperationItems, (item: CooperationItem) => {
          GridItem() {
            Column() {
              // 标题
              Text(item.title)
                .fontSize(18)
                .fontWeight(FontWeight.Bold)
                .fontColor('#222222')
                .margin({ bottom: 8 })
                .maxLines(1)
                .textOverflow({ overflow: TextOverflow.Ellipsis })

              // 描述
              Text(item.description)
                .fontSize(14)
                .fontColor('#666666')
                .textAlign(TextAlign.Center)
                .margin({ bottom: item.date ? 8 : 0 })
                .maxLines(3)
                .textOverflow({ overflow: TextOverflow.Ellipsis })

              // 日期和地点（如果有）
              if (item.date) {
                Text(item.date)
                  .fontSize(12)
                  .fontColor('#888888')
                  .margin({ bottom: 4 })
              }

              if (item.location) {
                Row() {
                  Image($r('app.media.ic_location'))
                    .width(12)
                    .height(12)
                    .margin({ right: 4 })
                  Text(item.location)
                    .fontSize(12)
                    .fontColor('#888888')
                }
              }
            }
            .width('100%')
            .height('100%')
            .padding(16)
            .backgroundColor(Color.White)
            .borderRadius(12)
            .shadow({
              radius: 4,
              color: '#00000010',
              offsetX: 0,
              offsetY: 2
            })
            .justifyContent(FlexAlign.SpaceAround)
          }
          .onClick(() => this.handleCooperationItemClick(item))
        })
      }
      .columnsTemplate('1fr 1fr')
      .rowsTemplate('1fr 1fr 1fr')
      .columnsGap(16)
      .rowsGap(16)
      .width('90%')
      .height('70%')
      .layoutWeight(1)

      // 底部版权信息
      Text('© 2023 测盟汇')
        .fontSize(12)
        .fontColor('#999999')
        .margin({ top: 20, bottom: 16 })
    }
    .width('100%')
    .height('100%')
    .padding(16)
    .backgroundColor('#f8f8f8')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }

  build() {
    Column() {
      // 顶部导航栏
      Row({ space: 10 }) {
        // 退出按钮
        Button() {
          Image($r('app.media.back')) // 退出图标
            .width(24)
            .height(24)
        }
        .type(ButtonType.Normal)
        .width(40)
        .height(40)
        .borderRadius(20)
        .backgroundColor('#f0f0f0')
        .onClick(() => this.logout())

        // 页面标题
        Text(this.pageTitle)
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .fontColor('#222222')
          .margin({ left: 10 })
          .layoutWeight(1)

        // 会议入口按钮
      }
      .width('100%')
      .padding(16)
      .alignItems(VerticalAlign.Center)
      .backgroundColor(Color.White)
      .shadow({
        radius: 4,
        color: '#00000010',
        offsetX: 0,
        offsetY: 2
      })

      // 主内容区域
      Column() {
        if (this.currentTab === 0) {
          this.renderIndustryNews()
        } else {
          this.renderCooperation()
        }
      }
      .layoutWeight(1)
      .width('100%')
      .backgroundColor('#f8f8f8')

      // 底部菜单栏 - 无图标，大字体粗体
      Row() {
        // 动态菜单项
        Column() {
          Text('动态')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .fontColor(this.currentTab === 0 ? '#4285f4' : '#666666')
        }
        .layoutWeight(1)
        .padding(15)
        .backgroundColor(this.currentTab === 0 ? '#e8f0fe' : Color.Transparent)
        .borderRadius(8)
        .onClick(() => {
          this.currentTab = 0;
        })

        // 合作菜单项
        Column() {
          Text('合作')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .fontColor(this.currentTab === 1 ? '#4285f4' : '#666666')
        }
        .layoutWeight(1)
        .padding(15)
        .backgroundColor(this.currentTab === 1 ? '#e8f0fe' : Color.Transparent)
        .borderRadius(8)
        .onClick(() => {
          this.currentTab = 1;
        })
      }
      .width('100%')
      .padding(10)
      .backgroundColor(Color.White)
      .shadow({
        radius: 4,
        color: '#00000010',
        offsetX: 0,
        offsetY: -2
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f8f8f8')
  }
}