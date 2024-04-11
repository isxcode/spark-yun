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
          <div class="link" @click="clickZhiqingyun">至轻云</div>
          <div class="link" @click="clickOther">至流云</div>
          <div class="link" @click="clickOther">至慧云</div>
          <div class="link" @click="clickOther">更多</div>
        </div>
        <div class="column">
          <div class="title">社区</div>
          <div class="link" @click="clickGitee">Gitee</div>
          <div class="link" @click="clickGithub">Github</div>
          <div class="link" @click="clickSlack">Slack</div>
          <div class="link" @click="clickDiscord">Discord</div>
          <div class="link" @click="clickLinked">Linkedin</div>
        </div>
        <div class="column">
          <div class="title">资源</div>
          <div class="link" @click="clickDocs">产品手册</div>
          <div class="link" @click="clickBlogs">博客</div>
          <div class="link" @click="clickFeishu">飞书文档</div>
          <div class="link" @click="clickDockerHub">Docker hub</div>
        </div>
        <div class="column">
          <div class="title">关于我们</div>
          <div class="link" @click="clickOther">至爻简介</div>
          <div class="link" @click="clickOther">加入我们</div>
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

  <div class="org-info">
    <div class="content">
      <div class="link">公司: 上海至爻数据有限公司</div>
      <div class="link">电话: 400-8848-170</div>
      <div class="link">邮箱: hello@isxcode.com</div>
      <div class="link">地址: 上海市徐汇区中山西路2020号501室</div>
    </div>
  </div>

  <div class="footer-copyright">
    <div class="content">
      <div class="equal-columns">
        <div>Copyright © 2023-2024 至爻数据</div>
        <div class="icp-click" @click="clickIcp">沪ICP备2023031895号-1</div>
        <div class="copyright-div">版权所有 上海至爻数据有限公司</div>
      </div>
    </div>
  </div>

  <div class="fixed-footer" @click="callPhoneNumber">
    <div class="content">
      <SvgIcon class="phone-svg" name="phone"></SvgIcon>
      <p class="phone-text"><a>电话咨询</a></p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.footer {
  font-family: "阿里巴巴普惠体 2.0 45 Light", sans-serif;
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
          cursor: pointer;
          margin-top: 22px;
          font-size: 14px;
        }
      }
    }
  }

}

.footer-copyright {
  font-family: "阿里巴巴普惠体 2.0 45 Light", sans-serif;
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
      .icp-click {
        cursor: pointer;
      }

      display: flex;
      flex: 1;
      justify-content: space-between;
    }
  }
}

.fixed-footer {
  display: none;
}

@font-face {
  font-family: "阿里妈妈数黑体 Bold";
  font-weight: 700;
  src: url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/T4JjC1yVtRsU.woff2") format("woff2"),
  url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/Y0ipOh97amn9.woff") format("woff");
  font-display: swap;
}

/* 在线链接服务仅供平台体验和调试使用，平台不承诺服务的稳定性，企业客户需下载字体包自行发布使用并做好备份。 */
@font-face {
  font-family: "阿里巴巴普惠体 2.0 45 Light";
  font-weight: 300;
  src: url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/cHPsZ2tb5Gxv.woff2") format("woff2"),
  url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/F8LRQLgsPF37.woff") format("woff");
  font-display: swap;
}

.org-info{
  display: none;
}

@media (max-width: 768px) {

  .fixed-footer {
    display: block;
    position: fixed;
    z-index: 999;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 50px;
    line-height: 50px;
    text-align: center;
    border-top: 1px solid #e5e5e5;
    background: #F9DED1;

    .content {
      width: 130px;
      display: flex;
      align-items: center;
      margin: 0 auto;

      .phone-svg {
        width: 24px;
        height: 24px;
      }

      .phone-text {
        margin-left: 10px;
        font-size: 1.25rem;
        color: #e25a1b;
      }
    }
  }

  .footer {
    padding-top: 40px;

    .content {
      width: 300px;
      height: 340px;
      margin: auto;
      display: flex;

      .left {
        width: 300px;

        .logo-img {
          margin: auto;
          width: 100px;
        }

        .logo-name {
          margin-top: 10px;
          width: 300px;
          text-align: center;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 28px;
        }

        .wechat-img {
          margin: 20px auto auto;
          width: 160px;
          height: 160px;
        }

        .wechat-name {
          width: 300px;
          text-align: center;
          font-size: 12px;
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

  .org-info {
    display: block;
    width: 100%;
    font-size: 14px;

    .content {
      height: 120px;
      line-height: 25px;
      width: 300px;
      margin: auto;
    }
  }

  .footer-copyright {
    width: 100%;
    border-top: lavender solid 1px;

    .content {
      font-size: 11px;
      height: 30px;
      line-height: 130px;
      color: grey;
      width: 300px;
      margin: auto;
      display: flex;

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
import {ElMessage} from 'element-plus'

function clickZhiqingyun() {
  const router = useRouter();
  router.push("/");
}

function clickOther() {
  ElMessage({
    message: '敬请期待',
    type: 'success',
  })
}

function clickGitee() {
  window.open("https://gitee.com/isxcode/spark-yun");
}

function clickGithub() {
  window.open("https://github.com/isxcode/spark-yun/discussions");
}

function clickSlack() {
  window.open("https://join.slack.com/t/zhiqingyun/shared_invite/zt-2fixm4lxg-Y5F1Qc6OQJjb7Z0kmcuQ0A");
}

function clickDiscord() {
  window.open("https://discord.gg/sEHPpmsp");
}

function clickLinked() {
  window.open("https://www.linkedin.com/groups/9802394/");
}

function clickDockerHub() {
  window.open("https://hub.docker.com/r/isxcode/zhiqingyun");
}

function clickFeishu() {
  window.open("https://mwur1opms2a.feishu.cn/wiki/space/7350313682586451971?ccm_open_type=lark_wiki_spaceLink&open_tab_from=wiki_home");
}

function clickBlogs() {
  window.open("/https://ispong.isxcode.com/tags/spark/");
}

function clickDocs() {
  const router = useRouter();
  router.push("/docs/zh/0/0");
}

function clickIcp() {
  window.open("https://beian.miit.gov.cn/");
}

function callPhoneNumber() {
  window.location.href = `tel:4008848170`;
}

</script>