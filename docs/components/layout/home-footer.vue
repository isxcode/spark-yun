<template>
  <div class="home-footer">
    <div class="source-content">
      <div class="w-20 left">
        <div class="logo">
          <img src="~assets/images/logo.png" alt="" />
          <h1 class="name">至爻云</h1>
        </div>
        <div class="wx-official-account">
          <img src="https://img.isxcode.com/picgo/wechat_zhiqingyun.jpg" />
          <div class="text">微信公众号</div>
        </div>
      </div>
      <div class="info">
        <div class="service">
          <div class="title">产品中心</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                至流云
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                至微云
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                至数云
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                至视云
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                至慧云
              </a>
            </div>
          </div>
        </div>

        <!-- 媒体 -->
        <div class="media">
          <div class="title">媒体</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                知乎
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                微博
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                B站
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                Discord
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                Twitter
              </a>
            </div>
          </div>
        </div>
        <div class="resource">
          <div class="title">资源社区</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                使用文档
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                Github
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                Gitee
              </a>
            </div>
            <div class="item">
              <a
                href="https://hub.docker.com/u/isxcode"
                target="_blank"
                rel="nofollow"
              >
                Docker hub
              </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                博客
              </a>
            </div>
            <!-- 常见问题 -->
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                常见问题
              </a>
            </div>
          </div>
        </div>

        <div class="company">
          <div class="title">关于我们</div>
          <div class="content">
            <div class="item">至爻简介</div>
            <div class="item">加入我们</div>
          </div>
        </div>

        <div class="contact">
          <div class="title">联系方式</div>
          <div class="content">
            <div class="item">电话：400-8848-170</div>
            <div class="item">邮箱：hello@isxcode.com</div>
            <div class="item">
              地址：上海市徐汇区中山西路2020号501、502、503、504室
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Coryright @2023-2024 至爻云
    沪CP备2023031895号
    沪公网安备2023031895号
    版权所有 上海至爻数据有限公司 -->

    <div class="s-footer-bottom">
      <span class="lg:w-1/6 sm:w-full"> Coryright @2023-2024 至爻云 </span>
      <a
        href="https://beian.miit.gov.cn/"
        target="_blank"
        rel="nofollow"
        class="footer-record lg:w-1/6 sm:w-full"
      >
        沪CP备2023031895号
      </a>
      <a
        href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=2023031895"
        target="_blank"
        rel="nofollow"
        class="lg:w-1/6 sm:w-full"
      >
        <i class="beian lg:w-1/6 sm:w-full"> </i>沪公网安备2023031895号</a
      >
      <span class="lg:w-1/6 sm:w-full"> 版权所有 上海至爻数据有限公司&nbsp; </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.home-footer {
  margin: 0 auto;
  background-color: #fff;
  border-top-width: 100%;
  .source-content {
    padding-top: 50px;
    padding-bottom: 50px;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    justify-content: center;
    .left {
      margin-left: 80px;
      display: flex;
      flex-direction: column;
      align-items: center;
      .logo {
        .name {
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 24px;
          text-align: center;
        }
      }
      .wx-official-account {
        width: 120%;
        margin-top: 20px;
        display: flex;
        flex-direction: column;
        align-items: center;
        .text {
          margin-top: 10px;
          font-size: 14px;
          color: #666;
        }
      }
    }
    .info {
      display: flex;
      justify-content: flex-start;
      padding: 0 20px;
      > div {
        margin-left: 54px;
        min-width: 86px;
      }
      .title {
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin-bottom: 20px;
      }
      .content {
        display: flex;
        flex-direction: column;
        .item {
          height: 18px;
          line-height: 18px;
          font-size: 14px;
          color: #666;
          margin-bottom: 10px;
          a {
            color: #666;
          }
        }
      }
    }
  }
  .s-footer {
    position: relative;
    width: 100%;
    min-width: 1140px;
  }
  .s-footer-bottom {
    margin: 0px auto;
    width: 100%;
    text-align: center;
    line-height: 35px;
    font-size: 14px;
    color: #666;
    > span,
    a {
      display: inline-block;
    }
  }
  .s-footer-bottom p {
    font-size: 13px;
    color: #666;
  }
  .s-footer-bottom p a {
    color: #666;
  }
  .s-footer-bottom p span {
    margin: 0 5px;
  }
}

@font-face {
  font-family: "阿里妈妈数黑体 Bold";
  font-weight: 700;
  src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/KV1x20rDnzTB.woff2")
      format("woff2"),
    url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/apdHoAjvEt5Q.woff")
      format("woff");
  font-display: swap;
}
</style>
