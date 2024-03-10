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
        <div v-if="!mobileFlag" class="service">
          <div class="title">产品中心</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 至流云 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 至微云 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 至数云 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 至视云 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 至慧云 </a>
            </div>
          </div>
        </div>

        <!-- 媒体 -->
        <div v-if="!mobileFlag" class="media">
          <div class="title">媒体</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 知乎 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 微博 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> B站 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> Discord </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> Twitter </a>
            </div>
          </div>
        </div>
        <div v-if="!mobileFlag" class="resource">
          <div class="title">资源社区</div>
          <div class="content">
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 使用文档 </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> Github </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> Gitee </a>
            </div>
            <div class="item">
              <a href="https://hub.docker.com/u/isxcode" target="_blank" rel="nofollow"> Docker hub </a>
            </div>
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 博客 </a>
            </div>
            <!-- 常见问题 -->
            <div class="item">
              <a href="https://zhiqingyun-demo.isxcode.com" target="_blank"> 常见问题 </a>
            </div>
          </div>
        </div>

        <div v-if="!mobileFlag" class="company">
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
            <div class="item">地址：上海市徐汇区中山西路2020号501、502、503、504室</div>
          </div>
        </div>
      </div>
    </div>

    <div class="s-footer-bottom">
      <span class="lg:w-1/6 sm:w-full sm:mr-4"> Coryright @2023-2024 至爻云 </span>
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="nofollow" class="footer-record lg:w-1/6 sm:w-full sm:mr-4"> 沪CP备2023031895号 </a>
      <a href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=2023031895" target="_blank" rel="nofollow" class="lg:w-1/6 sm:w-full sm:mr-4">
        <i class="beian lg:w-1/6 sm:w-full"> </i>
        沪公网安备2023031895号
      </a>
      <span class="lg:w-1/6 sm:w-full sm:mr-4"> 版权所有上海至爻数据有限公司&nbsp; </span>
    </div>
    <div v-if="mobileFlag" class="fixed-footer">电话咨询</div>
  </div>
</template>

<script lang="ts" setup>
import { isMobile } from "@/util/isMobile";
const mobileFlag = ref(false);
onMounted(async () => {
  await nextTick();
  mobileFlag.value = isMobile();
});
</script>

<style lang="scss" scoped>
.home-footer {
  margin: 0 auto;
  background-color: #fff;
  border-top-width: 100%;
  .source-content {
    padding-top: 3.125rem;
    padding-bottom: 3.125rem;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    justify-content: center;
    .left {
      margin-left: 5rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      .logo {
        .name {
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 1.5rem;
          text-align: center;
        }
      }
      .wx-official-account {
        width: 120%;
        margin-top: 1.25rem;
        display: flex;
        flex-direction: column;
        align-items: center;
        .text {
          margin-top: 0.625rem;
          font-size: 0.875rem;
          color: #666;
        }
      }
    }
    .info {
      display: flex;
      justify-content: flex-start;
      padding: 0 1.25rem;
      > div {
        margin-left: 3.375rem;
        min-width: 5.375rem;
      }
      .title {
        font-size: 1rem;
        font-weight: bold;
        color: #333;
        margin-bottom: 1.25rem;
      }
      .content {
        display: flex;
        flex-direction: column;
        .item {
          height: 1.125rem;
          line-height: 1.125rem;
          font-size: 0.875rem;
          color: #666;
          margin-bottom: 0.625rem;
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
    min-width: 71.25rem;
  }
  .s-footer-bottom {
    margin: 0rem auto;
    width: 100%;
    text-align: center;
    line-height: 2.1875rem;
    font-size: 0.875rem;
    color: #666;
    > span,
    a {
      display: inline-block;
    }
  }
  .s-footer-bottom p {
    font-size: 0.8125rem;
    color: #666;
  }
  .s-footer-bottom p a {
    color: #666;
  }
  .s-footer-bottom p span {
    margin: 0 0.3125rem;
  }
}

@font-face {
  font-family: "阿里妈妈数黑体 Bold";
  font-weight: 700;
  src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/KV1x20rDnzTB.woff2") format("woff2"), url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/apdHoAjvEt5Q.woff") format("woff");
  font-display: swap;
}

@media screen and (max-width: 475px) {
  .home-footer {
    margin: 0 auto;
    margin-bottom: 3.75rem;
    background-color: #fff;
    border-top-width: 100%;
    .source-content {
      padding-top: 3.125rem;
      padding-bottom: 3.125rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      .left {
        width: 16.125rem;
        margin-left: 0;
        display: flex;
        flex-direction: row;
        align-items: center;
        .logo {
          width: 6.125rem;
          .name {
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
            font-size: 1.5rem;
            text-align: center;
          }
        }
        .wx-official-account {
          margin-left: 2.2rem;
          width: 11rem;
          margin-top: 1.25rem;
          display: flex;
          flex-direction: column;
          align-items: center;
          .text {
            margin-top: 0.625rem;
            font-size: 0.875rem;
            color: #666;
          }
        }
      }
      .info {
        margin-top: 2rem;
        display: flex;
        justify-content: flex-start;
        padding: 0 1.25rem;
        > div {
          margin-left: unset;
          width: 20.375rem;
          min-width: unset;
        }
        .title {
          font-size: 1.2rem;
          font-weight: bold;
          color: #333;
          margin-bottom: 1.25rem;
        }
        .content {
          display: flex;
          flex-direction: column;
          .item {
            height: unset;
            line-height: 2rem;
            font-size: 1.2rem;
            margin-bottom: unset;
            a {
              color: #666;
            }
          }
        }
      }
    }
    .s-footer-bottom {
      margin: 0rem auto;
      width: 100%;
      text-align: center;
      line-height: 1rem;
      font-size: 0.875rem;
      color: #666;
      > span,
      a {
        margin-right: 0.2rem;
        display: inline-block;
        line-height: 1.2rem;
      }
    }
    .s-footer-bottom p {
      font-size: 0.8125rem;
      color: #666;
    }
    .s-footer-bottom p a {
      color: #666;
    }
    .s-footer-bottom p span {
      margin: 0 0.3125rem;
    }
    .fixed-footer {
      position: fixed;
      z-index: 999;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 3rem;
      line-height: 3rem;
      text-align: center;
      border-top: 1px solid #e5e5e5;
      font-size: 1.25rem;
      background: #f5dfd3;
      color: var(--sk-color-home-primary);
    }
  }
}
</style>
