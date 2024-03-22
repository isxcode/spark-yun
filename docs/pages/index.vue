<template>
  <div class="main">
    <div class="module-intro">
      <div class="content">
        <div class="left">
          <div class="name">
            至轻云
          </div>
          <div class="slogan">
            打造超轻量级大数据平台
          </div>
          <div class="command">
            <p id="command-shell">docker run -p 8080:8080 isxcode/zhiqingyun</p>
            <SvgIcon
              class="copy-icon"
              name="copy"
              @click="handleCommandCopyClick"
            ></SvgIcon>
          </div>
          <div class="btn-div">
            <div class="guide-btn">
              <p>快速入门</p>
            </div>
            <div class="quick-btn">
              <p>立即体验</p>
              <SvgIcon name="arrow-right" class="link-right-svg"></SvgIcon>
            </div>
            <div class="movie-btn">
              <p>产品视频</p>
              <SvgIcon name="arrow-right" class="link-right-svg"></SvgIcon>
            </div>
          </div>
        </div>
        <div class="right">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/product.png" alt="">
        </div>
      </div>
    </div>
    <div class="module-about">
      <div class="content">
        <div class="why-title">
          选择至轻云
        </div>
        <div class="why-content">
          至轻云，是一款超轻量级的企业级大数据平台. 提供开源Docker镜像,具备一键部署开箱即用特色. 且无需额外复杂的大数据组件安装.即可实现企业级数据离线/实时ETL，提供丰富的插件。持续更新迭代。轻量化、企业级软件，让企业快速处理海量数据，获得更多商业价值。
        </div>
        <div class="tech-title">
          相关技术
        </div>
        <div class="tech-img-div">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/k8s.png" alt="">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/hadoop.png" alt="">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/spark.png" alt="">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/hive.png" alt="">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/doris.png" alt="">
          <img class="tech-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/clickhouse.png" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-2.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">多平台部署，从不娇生惯养</div>
          <div class="line-2">支持大量计算引擎平台，如Kubernetes、Spark Standalone、Spark、Cluster、Yarn、CDH。可无缝迁移，无缝切换，帮助企业更优管理集群资源。</div>
          <div class="line-3">立即体验</div>
        </div>
      </div>
    </div>
    <div class="module-feat-right">
      <div class="content">
        <div class="right-phone">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-2.jpg" alt="">
        </div>
        <div class="left">
          <div class="line-1">驱动上传，任何姿势都能连</div>
          <div class="line-2">支持海量数据源，如Mysql、Hive、Oracle、SqlServer、TiDB、Oceanbase、Doris、Clickhouse等。用户可自定义上传多版本驱动，解决企业中多环境数据源无法同时连接的难题。</div>
          <div class="line-3">立即体验</div>
        </div>
        <div class="right">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-2.jpg" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-2.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">创建租户，再大也能装下</div>
          <div class="line-2">多租户模式，确保租户间数据隔离。租户内可单独协调集群资源、数据源驱动、作业流配置等。解决企业中规模较大、用户众多、权限难解的问题。</div>
          <div class="line-3">立即体验</div>
        </div>
      </div>
    </div>
    <div class="module-business">
      <div class="content">
        <div class="line-1">数据创造价值，大数据创造大价值</div>
        <div class="line-2">AI将至，至轻云与您携手实现企业级大数据平台落地</div>
        <div class="business-img">
          <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-1.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-2.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-3.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-4.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-5.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-6.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-7.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-8.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-9.png" alt="">
          <img class="bus-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/b-10.png" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-right">
      <div class="content">
        <div class="right-phone">
          <img class="feat-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-4.jpg" alt="">
        </div>
        <div class="left">
          <div class="line-1">编排作业，横竖都能拖拽</div>
          <div class="line-2">拥有复杂的作业执行命令，包括运行、中止、重跑、重跑下游、中断、重跑当前、发布等。且支持可视化Cron表达式调度配置，解决企业中各种复杂应用场景。</div>
          <div class="line-3">立即体验</div>
        </div>
        <div class="right">
          <img class="feat-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-4.jpg" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img class="feat-img" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/p-5.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">能写代码？不单单只有SparkSql</div>
          <div class="line-2">超多作业类型，包括数据同步、Bash、Python、SparkSql、Jdbc、自定义Jar等。提供客户二次开发模版，任何复杂的场景都可以通过写代码的方式解决。</div>
          <div class="line-3">立即体验</div>
        </div>
      </div>
    </div>
    <div class="module-end">
      <div class="content">
        <div class="line-1">开源助力企业升级大数据能力</div>
        <div class="end-btn">免费试用</div>
      </div>
    </div>
  </div>
  <LayoutHomeFooter/>
</template>

<script lang="ts" setup>
definePageMeta({
  title: "首页",
  layout: "home",
});

useSeoMeta({
  title: "至轻云",
  ogTitle: "至轻云",
  description: "打造超轻量级大数据平台",
  ogDescription: "打造超轻量级大数据平台",
});

function handleCommandCopyClick() {
  const codeBlock = document.getElementById("command-shell");
  const codeBlockText = codeBlock?.innerText;
  if (codeBlockText) {
    copyContent(codeBlockText);
  }
}

const copyContent = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
  } catch (err) {
    console.error("Failed to copy: ", err);
  }
};

</script>

<style lang="scss" scoped>


$font-size: 14px;
// 介绍的高度
$module-intro-height: 850px;
// 相关技术板块的高度
$module-about-height: 460px;
// 商业板块的高度
$module-business-height: 550px;
// 主题色
$primary-color: #e25a1b;
// 特点1背景色
$module-feat-left-bg-color: white;
// 特点2背景色
$module-feat-right-bg-color: rgba(255, 156, 110, 5%);
// 特点高度
$module-feat-height: 500px;
// 结束板块高度
$module-end-height: 300px;
// 结束板块高度
$primary-width: 1200px;
// 产品介绍图片大小
$module-intro-img-width: 600px;


.main {

  .module-intro {
    width: 100%;
    height: $module-intro-height;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/bg4.png');
    padding-top: 200px;

    .content {
      display: flex;

      .left {
        width: 600px;;

        .name {
          margin-top: 120px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 60px;
        }

        .slogan {
          margin-top: 20px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 40px;
        }

        .command {
          display: flex;
          margin-top: 20px;
          padding-left: 15px;
          border: #e25a1b solid 1px;
          color: #e25a1b;
          width: 435px;
          border-radius: 3px;
          font-size: 18px;
          height: 38px;
          line-height: 38px;

          .copy-icon {
            margin-left: 15px;
            margin-top: 6px;
            width: 24px;
            height: 24px;
          }
        }

        .btn-div {
          margin-top: 25px;
          display: flex;

          .guide-btn {
            width: 120px;
            border-radius: 3px;
            background: #e25a1b;
            color: white;
            text-align: center;
            font-size: 18px;
            height: 40px;
            line-height: 40px;
          }

          .movie-btn {
            display: none;
          }

          .quick-btn {
            margin-left: 20px;
            width: 150px;
            color: #e25a1b;
            height: 40px;
            line-height: 40px;
            text-align: center;
            display: flex;

            .link-right-svg {
              margin-top: 10px;
              width: 20px;
              height: 20px;
            }
          }
        }
      }

      .right {
        width: 600px;;

        img {
          width: $module-intro-img-width;
        }
      }
    }
  }

  .module-about {
    width: 100%;
    background: white;
    height: $module-about-height;
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 40px;

    .content {
      .why-title {
        margin: auto;
        text-align: center;
        width: 400px;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .why-content {
        margin: 25px auto auto;
        text-indent: 2em;
        width: 800px;
        line-height: 22px;
        font-size: 17px;
      }

      .tech-title {
        text-align: center;
        margin: 40px auto auto;
        width: 400px;
        font-size: 12px;
        color: lightgray;
      }

      .tech-img-div {
        display: flex;
        justify-content: space-between;
        margin: 20px auto auto;
        width: 1200px;

        img {
          width: 150px;
          height: 55px;
        }
      }
    }
  }

  .module-business {
    width: 100%;
    height: $module-business-height;
    background: $primary-color;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      .line-1 {
        margin-top: 50px;
        color: white;
        text-align: center;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 35px;
      }

      .line-2 {
        margin-top: 16px;
        color: white;
        text-align: center;
      }

      .business-img {
        margin: 40px auto auto;
        width: 1200px;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;

        img {
          width: 220px;
          height: 150px;
          margin: 6px;
        }
      }
    }
  }
}

.module-feat-left {
  width: 100%;
  height: $module-feat-height;
  background: $module-feat-right-bg-color;
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    display: flex;

    .left {
      width: 600px;;

      img {
        width: 450px;
      }
    }

    .right {
      margin-left: 100px;
      margin-top: 55px;
      width: 600px;;

      .line-1 {
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .line-2 {
        width: 560px;
        height: 55px;
        margin-top: 30px;
        line-height: 22px;
      }

      .line-3 {
        margin-top: 30px;
        color: #e25a1b;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
      }
    }
  }

}

.module-feat-right {
  width: 100%;
  height: $module-feat-height;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $module-feat-left-bg-color;

  .content {
    display: flex;

    .right-phone {
      display: none;
    }

    .right {
      width: 600px;
      margin-left: 100px;

      img {
        width: 450px;
      }
    }

    .left {
      width: 600px;;
      margin-top: 55px;

      .line-1 {
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .line-2 {
        width: 560px;
        height: 55px;
        margin-top: 30px;
        line-height: 22px;
      }

      .line-3 {
        margin-top: 30px;
        color: #e25a1b;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
      }
    }
  }
}

.module-end {
  height: $module-end-height;
  background: #e25a1b;
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    .line-1 {
      width: 600px;
      text-align: center;
      color: white;
      margin: auto;
      font-family: "阿里妈妈数黑体 Bold", sans-serif;
      font-size: 40px;
    }

    .end-btn {
      margin: 40px auto auto;
      background: white;
      color: #e25a1b;
      width: 200px;
      font-size: 20px;
      border-radius: 3px;
      height: 40px;
      line-height: 40px;
      text-align: center;
    }
  }
}

.content {
  margin: auto;
  width: $primary-width;
}

@font-face {
  font-family: "阿里妈妈数黑体 Bold";
  font-weight: 700;
  src: url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/T4JjC1yVtRsU.woff2") format("woff2"),
  url("//at.alicdn.com/wf/webfont/UMV2yX61q8rB/Y0ipOh97amn9.woff") format("woff");
  font-display: swap;
}

// ------------------------------------------------------- 移动端 ----------------------------------------------------------------------

@media (max-width: 768px) {

  .main {

    .module-intro {
      width: 100%;
      height: 850px;
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      background-image: url('https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/bg4.png');
      padding-top: 400px;

      .content {
        display: flex;

        .left {
          width: 400px;

          .name {
            text-align: center;
            margin-top: 120px;
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
            font-size: 60px;
          }

          .slogan {
            margin-top: 20px;
            text-align: center;
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
            font-size: 25px;
          }

          .command {
            display: none;
          }

          .btn-div {
            display: flex;
            width: 300px;
            margin: 25px auto auto;

            .guide-btn {
              width: 120px;
              border-radius: 3px;
              background: #e25a1b;
              color: white;
              text-align: center;
              font-size: 18px;
              height: 40px;
              line-height: 40px;
              margin-left: 50px;
            }

            .quick-btn {
              display: none;
            }

            .movie-btn {
              margin-left: 30px;
              width: 150px;
              color: #e25a1b;
              height: 40px;
              line-height: 40px;
              text-align: center;
              display: flex;

              .link-right-svg {
                margin-top: 10px;
                width: 20px;
                height: 20px;
              }
            }
          }
        }

        .right {
          display: none;
          width: 600px;;

          img {
            width: $module-intro-img-width;
          }
        }
      }
    }

    .module-about {
      width: 100%;
      background: white;
      height: 350px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding-top: 40px;

      .content {
        .why-title {
          margin: auto;
          text-align: center;
          width: 300px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 30px;
        }

        .why-content {
          margin: 25px auto auto;
          text-indent: 2em;
          width: 300px;
          line-height: 22px;
          font-size: 17px;
        }

        .tech-title {
          display: none;
        }

        .tech-img-div {
          display: none;
        }
      }
    }

    .module-business {
      width: 100%;
      height: 320px;
      background: $primary-color;
      align-items: center;

      .content {
        height: 350px;

        .line-1 {
          color: white;
          text-align: center;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 20px;
        }

        .line-2 {
          margin-top: 16px;
          color: white;
          text-align: center;
          font-size: 12px;
        }

        .business-img {
          margin: 20px auto auto;
          width: 300px;

          .bus-img {
            display: none;
          }

          img {
            width: 200px;
          }
        }
      }
    }
  }

  .module-feat-left {
    width: 100%;
    height: 450px;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      height: 360px;
      flex-direction: column;

      .left {
        width: 300px;

        img {
          width: 300px;
        }
      }

      .right {
        margin-left: 0;
        margin-top: 20px;
        width: 300px;

        .line-1 {
          margin-top: 10px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 25px;
          text-align: center;
        }

        .line-2 {
          height: 100px;
          width: 300px;
          margin-top: 30px;
          line-height: 22px;
        }

        .line-3 {
          display: none;
        }
      }
    }

  }

  .module-feat-right {

    width: 100%;
    height: 450px;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      height: 360px;
      flex-direction: column;

      .right {
        display: none;
      }

      .right-phone {
        display: block;

        img {
          width: 300px;
        }
      }

      .left {
        margin-left: 0;
        margin-top: 20px;
        width: 300px;

        .line-1 {
          margin-top: 10px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 25px;
          text-align: center;
        }

        .line-2 {
          height: 100px;
          width: 300px;
          margin-top: 30px;
          line-height: 22px;
        }

        .line-3 {
          display: none;
        }
      }
    }
  }

  .module-end {
    height: $module-end-height;
    background: #e25a1b;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      .line-1 {
        width: 300px;
        text-align: center;
        color: white;
        margin: auto;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 40px;
      }

      .end-btn {
        display: none;
      }
    }
  }

  .content {
    margin: auto;
    width: 300px;
  }

}
</style>
