<template>
  <div class="footer">

    <div class="content">
      <div class="left">
        <img class="logo-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/logo.jpg" alt=""/>
        <div class="logo-name">
          {{ $t("company_name") }}
        </div>
        <img class="wechat-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/qrcode.jpg" alt=""/>
        <div class="wechat-name">
          {{ $t("wechat_official_account") }}
        </div>
      </div>
      <div class="right">
        <div class="column">
          <div class="title">{{ $t("product") }}</div>
          <div class="link" @click="clickZhiqingyun">{{ $t("zhiqingyun") }}</div>
          <div class="link" @click="clickZhishuyun">{{ $t("zhishuyun") }}</div>
        </div>
        <div class="column">
          <div class="title">{{ $t("community") }}</div>
          <div class="link" @click="clickGithub">{{ $t("github") }}</div>
          <div class="link" @click="clickGitee">{{ $t("gitee") }}</div>
          <div class="link" @click="clickDiscussions">{{ $t("discord") }}</div>
        </div>
        <div class="column">
          <div class="title">{{ $t("resources") }}</div>
          <div class="link" @click="clickDocs">{{ $t("docs") }}</div>
          <div class="link" @click="clickBlogs">{{ $t("blogs") }}</div>
          <div class="link" @click="downloadPackage">{{ $t("download_package") }}</div>
          <div class="link" @click="downloadLicense"> {{ $t("download_license") }}</div>
        </div>
        <div class="column">
          <div class="title">{{ $t("about_us") }}</div>
          <div class="link" @click="aboutUs">{{ $t("company_intro") }}</div>
          <div class="link" @click="clickGithubProject"> {{ $t("development_progress") }}</div>
          <div class="link" @click="downloadPdf"> {{ $t("download_pdf") }}</div>
        </div>
        <div class="column">
          <div class="title">{{ $t("contact_us") }}</div>
          <div class="link">{{ $t("phone") }}</div>
          <div class="link">{{ $t("wx_chat") }}</div>
          <div class="link">{{ $t("email") }}hello@isxcode.com</div>
          <div class="link">{{ $t("address") }}</div>
        </div>
      </div>
    </div>
  </div>

  <div class="org-info">
    <div class="content">
      <div class="link">{{ $t("phone_company_name") }}</div>
      <div class="link">{{ $t("phone") }}</div>
      <div class="link">{{ $t("wx_chat") }}</div>
      <div class="link">{{ $t("email") }}hello@isxcode.com</div>
      <div class="link">{{ $t("address") }}</div>
    </div>
  </div>

  <div class="footer-copyright">
    <div class="content">
      <div class="equal-columns">
        <div>{{ $t("copyright") }}</div>
        <div class="icp-click" @click="clickIcp">{{ $t("icp") }}</div>
        <div class="copyright-div">{{ $t("reserved_rights") }}</div>
      </div>
    </div>
  </div>

  <div class="fixed-footer" @click="callPhoneNumber">
    <div class="content">
      <SvgIcon class="phone-svg" name="phone"></SvgIcon>
      <p class="phone-text"><a>{{ $t("phone_inquiry") }}</a></p>
    </div>
  </div>

  <!-- License Dialog -->
  <el-dialog
    v-model="licenseDialogVisible"
    width="500px"
    align-center
    :show-close="true"
  >
    <div class="license-dialog-content">
      <img class="license-qrcode" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/qrcode.jpg" alt="微信公众号二维码"/>
      <p class="license-tip">扫码添加微信公众号，获取体验许可证</p>
    </div>
  </el-dialog>
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

.org-info {
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
    background: #ffccb4;

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
      height: 130px;
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

.license-dialog-content {
  text-align: center;
  padding: 20px 0;

  .license-qrcode {
    width: 200px;
    height: 200px;
    margin: 0 auto 20px;
    display: block;
  }

  .license-tip {
    font-size: 16px;
    color: #e25a1b;
    line-height: 1.6;
  }
}
</style>
<script setup lang="ts">
import {ref} from "vue";
import {ElMessage} from "element-plus";

const switchLocalePath = useSwitchLocalePath();
const {locale} = useI18n();

// License dialog state
const licenseDialogVisible = ref(false);

// function clickZhiqingyun() {
//   const router = useRouter();
//   const newLocale = locale.value === "en" ? "zh" : "en";
//   router.push({path: switchLocalePath(newLocale)});
// }

function clickZhiqingyun() {
  const router = useRouter();
  router.push("/");
}

function clickZhiliuyun() {
  window.open("https://zhiliuyun.isxcode.com");
}

function clickZhishiyun() {
  window.open("https://zhishiyun.isxcode.com");
}

function clickZhihuiyun() {
  window.open("https://zhihuiyun.isxcode.com");
}

function clickZhishuyun() {
  window.open("https://zhishuyun.isxcode.com");
}

function joinUs() {
  const router = useRouter();
  router.push("/zh/docs/zh/5/13");
}

function aboutUs() {
  const router = useRouter();
  router.push("/zh/docs/zh/6/0");
}

function clickGithubProject() {
  window.open("https://github.com/orgs/isxcode/projects/16");
}

function clickGitee() {
  window.open("https://gitee.com/isxcode/spark-yun/issues");
}

function clickGithub() {
  window.open("https://github.com/isxcode/spark-yun/issues");
}

function clickSlack() {
  window.open("https://join.slack.com/t/isxcode/shared_invite/zt-2k9pnkm68-6hJo9e0Mp1yax2mNYv8caA");
}

function clickDiscord() {
  window.open("https://discord.gg/2AkVmn86");
}

function clickDiscussions() {
  window.open("https://github.com/isxcode/spark-yun/discussions");
}

function clickLinked() {
  window.open("https://www.linkedin.com/groups/9802394/");
}

function clickDockerHub() {
  window.open("https://hub.docker.com/r/isxcode/zhiqingyun");
}

function downloadLicense() {
  licenseDialogVisible.value = true;
}

function downloadPdf() {
  window.open("https://zhiqingyun-demo.isxcode.com/tools/open/file/zhiqingyun.pdf");
}

function downloadPackage() {
  window.open("https://zhiqingyun-demo.isxcode.com/tools/open/file/zhiqingyun.tar.gz");
}

function clickBlogs() {
  window.open("https://ispong.isxcode.com");
}

function clickDocs() {
  const router = useRouter();
  router.push("/zh/docs/zh/2/0");
}

function clickIcp() {
  window.open("https://beian.miit.gov.cn/");
}

function callPhoneNumber() {
  window.location.href = `tel:18994591261`;
}

</script>