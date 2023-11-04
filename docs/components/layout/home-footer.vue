<template>
  <div class="home-footer">
    <div class="source-content">
      <div class="logo">
        <img src="~assets/images/logo.png" alt="" />
      </div>
      <div class="info">
        <!-- <div class="service">
          <div class="title">服务</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank">
                演示环境
              </a>
            </div>
            <div class="item">
              <a href="http://localhost:3000/install/docker-deploy">
                文档中心
              </a>
            </div>
          </div>
        </div> -->

        <!-- <div class="company">
        <div class="title">公司</div>
        <div class="content">
          <div class="item">关于我们</div>
          <div class="item">加入我们</div>
          <div class="item">成长历程</div>
          <div class="item">媒体报道</div>
          <div class="item">隐私协议</div>
        </div>
      </div> -->

        <div class="contact">
          <div class="title">联系我们</div>
          <div class="content">
            <div class="item">hello@isxcode.com</div>
            <div class="item">ispong@outlook.com</div>
            <!-- <div class="item">
              <a href="https://github.com/isxcode/spark-yun" target="_blank">
                github
              </a>
            </div> -->
          </div>
        </div>
      </div>
      <div class="wx-img">
        <img src="https://img.isxcode.com/picgo/wechat_zhiqingyun.jpg" />
      </div>
    </div>
    <div class="s-footer-bottom">
      <!-- <p>
        上海至爻数据有限公司&nbsp;
        <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"></a>
        <a
          href="https://beian.miit.gov.cn/"
          target="_blank"
          rel="nofollow"
          class="footer-record"
        >
          闽ICP备15016281号-3
        </a>
        <span>|</span>
        <a
          href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=35020302033924"
          target="_blank"
          rel="nofollow"
        >
          <i class="beian"> </i>闽公网安备35020302033924号</a
        >
      </p> -->
      <!-- <p class="text-gray-500 dark:text-gray-400 text-sm">
        Copyright © 2023 spark-cloud -
        <a
          href="https://github.com/nuxt/nuxt/blob/main/LICENSE"
          rel="noopener noreferrer"
          target="_blank"
          class="hover:underline"
        >
          All rights reserved
        </a>
      </p> -->
    </div>
  </div>
</template>


<style lang="scss" scoped>
.home-footer {
  margin: 0 auto;
  width: 1200px;
  background-color: #fff;
  border-top-width: 100%;
  .source-content {
    padding-top: 30px;
    padding-bottom: 30px;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    justify-content: center;
    .logo {
      width: 67px;
      margin-left: 80px;
    }
    .info {
      width: 62%;
      display: flex;
      justify-content: flex-start;
      padding: 0 20px;
      > div {
        margin-left: 24px;
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
    .wx-img {
      width: 94px;
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
</style>
