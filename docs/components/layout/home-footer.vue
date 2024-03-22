<template>

  <div class="footer">

    <div class="content">
      <div class="left">
        <img class="logo-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/zhiqingyun-logo.jpg" alt=""/>
        <div class="logo-name">
          至爻数据
        </div>
        <img class="wechat-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiyao/qrcode_for_12.jpg" alt=""/>
        <div class="wechat-name">
          微信公众号
        </div>
      </div>
      <div class="right">
        <div class="column">
          <div class="title">产品</div>
          <div class="link">至轻云</div>
          <div class="link">至流云</div>
          <div class="link">至慧云</div>
          <div class="link">更多</div>
        </div>
        <div class="column">
          <div class="title">社区</div>
          <div class="link">Gitee</div>
          <div class="link">Github</div>
          <div class="link">Slack</div>
          <div class="link">Discord</div>
          <div class="link">Linked</div>
        </div>
        <div class="column">
          <div class="title">资源</div>
          <div class="link">产品手册</div>
          <div class="link">博客</div>
          <div class="link">常见问题</div>
          <div class="link">Docker hub</div>
        </div>
        <div class="column">
          <div class="title">关于我们</div>
          <div class="link">至爻简介</div>
          <div class="link">加入我们</div>
        </div>
        <div class="column">
          <div class="title">联系我们</div>
          <div class="link">电话: 400-8848-170</div>
          <div class="link">邮箱: hello@isxcode.com</div>
          <div class="link">地址: 上海市徐汇区中山西路2020号501室</div>
        </div>
      </div>
    </div>
  </div>

  <div class="footer-copyright">
    <div class="content">
      <div class="equal-columns">
        <div>Copyright © 2023-2024 至爻数据</div>
        <div>沪ICP备2023031895号</div>
        <div class="copyright-div">版权所有 上海至爻数据有限公司</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.footer {
  padding-top: 40px;

  .content {
    width: 1200px;
    height: 300px;
    margin: auto;
    display: flex;

    .left {
      width: 300px;

      .logo-img {
        margin: auto;
        width: 80px;
      }

      .logo-name {
        margin-top: 10px;
        width: 300px;
        text-align: center;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 18px;
      }

      .wechat-img {
        margin: 20px auto auto;
        width: 120px;
        height: 120px;
      }

      .wechat-name {
        width: 300px;
        text-align: center;
        font-size: 10px;
        color: grey;
      }
    }

    .right {
      margin-left: 120px;
      width: 780px;
      display: flex;

      .column {
        display: flex;
        flex-direction: column;
        margin-right: 40px;

        .title {
          width: 75px;
          font-size: 18px;
          font-weight: bold;
        }

        .link {
          margin-top: 22px;
          font-size: 14px;
        }
      }
    }
  }

}

.footer-copyright {
  width: 100%;
  border-top: lavender solid 1px;

  .content {
    font-size: 12px;
    height: 30px;
    color: grey;
    width: 1200px;
    margin: auto;
    display: flex;
    align-items: center;
    justify-content: center;

    .equal-columns {
      display: flex;
      flex: 1;
      justify-content: space-between;
    }
  }
}

@font-face {
  font-family: "阿里妈妈数黑体 Bold";
  font-weight: 700;
  src: url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/T4JjC1yVtRsU.woff2") format("woff2"),
  url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/Y0ipOh97amn9.woff") format("woff");
  font-display: swap;
}

@media (max-width: 400px) {
  .footer {
    padding-top: 40px;

    .content {
      width: 300px;
      height: 300px;
      margin: auto;
      display: flex;

      .left {
        width: 300px;

        .logo-img {
          margin: auto;
          width: 80px;
        }

        .logo-name {
          margin-top: 10px;
          width: 300px;
          text-align: center;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 18px;
        }

        .wechat-img {
          margin: 20px auto auto;
          width: 120px;
          height: 120px;
        }

        .wechat-name {
          width: 300px;
          text-align: center;
          font-size: 10px;
          color: grey;
        }
      }

      .right {
        display: none;

        .column {
          display: flex;
          flex-direction: column;
          margin-right: 40px;

          .title {
            width: 75px;
            font-size: 18px;
            font-weight: bold;
          }

          .link {
            margin-top: 22px;
            font-size: 14px;
          }
        }
      }
    }

  }

  .footer-copyright {
    width: 100%;
    border-top: lavender solid 1px;

    .content {
      font-size: 11px;
      height: 30px;
      color: grey;
      width: 300px;
      margin: auto;
      display: flex;
      align-items: center;
      justify-content: center;

      .equal-columns {
        display: flex;
        flex: 1;
        justify-content: space-between;
      }

      .copyright-div {
        display: none;
      }
    }
  }
}
</style>
<script setup lang="ts">

</script>