<template>
  <div class="main">
    <!-- 一句话产品 -->
    <div class="one-word-desc-product">
      <div class="top">
        <div class="one-word">至轻云-超轻量级大数据平台</div>
        <div class="one-desc">•企业级平台 &nbsp; &nbsp; •Docker一键部署 &nbsp; &nbsp; •Github开源免费 &nbsp; &nbsp; •Spark纯原生</div>
        <div class="fast-use" id="code_block">
          <span class="line" line="1">
            <span style="color: #e25a1b">docker</span>
            <span style="color: #e25a1b"> run</span>
            <span style="color: #e25a1b"> -p</span>
            <span style="color: #e25a1b"> 8080</span>
            <span style="color: #e25a1b">:8080</span>
            <span style="color: #e25a1b"> -d</span>
            <span style="color: #e25a1b"> isxcode/zhiqingyun</span>
            <SvgIcon class="copy-icon" name="copy" @click="handleCommandCopyClick"></SvgIcon>
          </span>
        </div>
        <div class="product-img">
          <img src="https://img.isxcode.com/picgo/20231102213942.png" alt="" />
        </div>
      </div>
      <div class="bottom">
        <div class="opt-detail">
          <el-button class="w-32 !h-9" type="primary" @click="jumpDoc"> 快速入门 </el-button>
          <el-button
            class="w-32 ml-btn !h-9"
            type="primary"
            @click="
              handleAdvantageToUse({
                url: 'https://zhiqingyun-demo.isxcode.com',
              })
            "
          >
            立即体验
          </el-button>
        </div>
      </div>
      <video id="v1" autoplay loop muted>
        <source src="~/assets/home/home.mp4" type="video/mp4" />
      </video>
    </div>
    <!-- 产品优势 -->
    <div
      v-for="(advantageItem, index) in productAdvantagesList"
      :key="index"
      class="product-advantages"
      :class="{
        'product-advantages-bgc': advantageItem.layout === 'left',
      }"
    >
      <div
        class="advantages"
        :class="{
          left: advantageItem.layout === 'left',
          right: advantageItem.layout === 'right',
        }"
      >
        <div class="img">
          <img :src="advantageItem.img" alt="" />
        </div>
        <div class="superior-panel">
          <div class="introduction">
            <div class="title">{{ advantageItem.title }}</div>
            <div class="desc">
              {{ advantageItem.desc }}
            </div>
          </div>
          <div class="to-use" @click="handleAdvantageToUse(advantageItem)">
            <span class="txt"> 快速使用 &nbsp; </span>
          </div>
        </div>
      </div>
    </div>
    <!-- 产品资源 -->
    <div class="product-resources">
      <div class="title">数据创造价值，大数据创造大价值</div>
      <div class="desc">AI将至，至轻云与您携手实现企业级大数据平台落地</div>
      <div class="img-row w-11/12 2xl:w-8/12">
        <!-- <div
          class="resource-item w-2/12"
          v-for="(item, index) in resourceList"
          :key="index"
          @click="handleResourcesImgClick(item)"
        >
          <img :src="item.imgUrl" alt="" />
        </div> -->
      </div>
    </div>
    <!-- 产品服务 -->
    <div class="product-service border-bottom-br">
      <div
        v-for="(serviceItem, index) in productServiceList"
        :key="index"
        class="product-advantages"
        :class="{
          'product-advantages-bgc': serviceItem.layout === 'left',
        }"
      >
        <div
          class="advantages"
          :class="{
            left: serviceItem.layout === 'left',
            right: serviceItem.layout === 'right',
          }"
        >
          <div class="img">
            <img :src="serviceItem.img" alt="" />
          </div>
          <div class="superior-panel">
            <div class="introduction">
              <div class="title">{{ serviceItem.title }}</div>
              <div class="desc">
                {{ serviceItem.desc }}
              </div>
            </div>
            <div class="to-use" @click="handleServiceToUse(serviceItem)">
              <span class="txt"> 快速使用 &nbsp; </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 号召使用 -->
    <div class="quick-use">
      <div class="title">开源助力企业升级大数据领域</div>
      <!-- 按钮 免费试用 -->
      <el-button
        class="w-32 !h-9 quick-use-btn"
        type="primary"
        @click="
          handleAdvantageToUse({
            url: 'https://zhiqingyun-demo.isxcode.com',
          })
        "
      >
        免费试用
      </el-button>
    </div>
    <LayoutHomeFooter />
  </div>
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

onMounted(async () => {
  await nextTick();
  const video = document.getElementById("v1") as HTMLVideoElement;
  if (video) {
    video.playbackRate = 0.5;
  } else {
    console.error("Video element not found");
  }
});

const productAdvantagesList = reactive([
  {
    code: "1",
    layout: "left",
    title: "多平台部署，从不娇生惯养",
    desc: "支持大量计算引擎平台，如Kubernetes、Spark Standalone、Spark Cluster、Yarn、CDH。可无缝迁移，无缝切换，帮助企业更优管理集群资源。",
    img: "https://img.isxcode.com/picgo/zhiqingyun1.jpg",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    code: "2",
    layout: "right",
    title: "驱动上传，任何姿势都能连",
    desc: "支持海量数据源，如Mysql、Hive、Oracle、SqlServer、TiDB、Oceanbase、Doris、Clickhouse等。用户可自定义上传多版本驱动，解决企业中多环境数据源无法同时连接的难题。",
    img: "https://img.isxcode.com/picgo/zhiqingyun2.jpg",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    code: "3",
    layout: "left",
    title: "创建租户，再大也能装下",
    desc: "多租户模式，确保租户间数据隔离。租户内可单独协调集群资源、数据源驱动、作业流配置等。解决企业中规模较大、用户众多、权限难解的问题。",
    img: "https://img.isxcode.com/picgo/zhiqingyun3.jpg",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
]);

function handleAdvantageToUse(item: { url: string | URL | undefined }) {
  window.open(item.url);
}

const productServiceList = reactive([
  {
    code: "1",
    layout: "right",
    title: "编排作业，横竖都能拖拽",
    desc: "拥有复杂的作业执行命令，包括运行、中止、重跑、重跑下游、中断、重跑当前、发布等。且支持可视化Cron表达式调度配置，解决企业中各种复杂应用场景。",
    img: "https://img.isxcode.com/picgo/zhiqingyun4.jpg",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    code: "2",
    layout: "left",
    title: "能写代码？不单单只有SparkSql",
    desc: "超多作业类型，包括数据同步、Bash、Python、SparkSql、Jdbc、自定义Jar等。提供客户二次开发模版，任何复杂的场景都可以通过写代码的方式解决。",
    img: "https://img.isxcode.com/picgo/zhiqingyun5.jpg",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
]);

function handleServiceToUse(serviceItem: { url: string | URL | undefined }) {
  window.open(serviceItem.url);
}

const copyContent = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
  } catch (err) {
    console.error("Failed to copy: ", err);
  }
};

function handleCommandCopyClick() {
  const codeBlock = document.getElementById("code_block");
  const codeBlockText = codeBlock?.innerText;
  if (codeBlockText) {
    copyContent(codeBlockText);
  }
}

function jumpDoc() {
  const router = useRouter();
  router.push("/docs/zh/install/docker-deploy");
}
</script>

<style lang="scss" scope>
.main {
  .one-word-desc-product {
    position: relative;
    padding-top: 12vh;
    flex-direction: column;
    #v1 {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      z-index: -1;
    }
    .top {
      height: calc(100vh - 20vh);
      flex: 1;
      display: flex;
      align-items: center;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      align-items: center;
      .one-word {
        font-size: 3.4375rem;
        font-weight: 700;
        text-align: center;
        line-height: 1.5;
        margin-bottom: 5vh;
      }
      .one-desc {
        font-size: 1.1875rem;
        font-weight: 400;
        text-align: center;
        line-height: 1.5;
        margin-bottom: 4vh;
        color: var(--el-color-info);
      }
      .fast-use {
        font-size: 1.5rem;
        font-weight: 400;
        text-align: center;
        line-height: 1.5;
        margin-bottom: 4vh;
      }
      .product-img {
        z-index: 9;
        position: absolute;
        bottom: 10vh;
        display: flex;
        justify-content: center;
        > img {
          width: 100%;
          height: 58vh;
          box-shadow: var(--sk-box-show);
          border-radius: 0.25rem;
        }
      }
    }
    .bottom {
      min-height: 20vh;
      width: 100%;
      background-image: url("~/assets/home/bg0.png");
      background-repeat: no-repeat;
      background-size: 100% auto;
      display: flex;
      flex-direction: column-reverse;
      .opt-detail {
        display: flex;
        justify-content: center;
        margin-bottom: 3vh;
        > button {
          background: #fff;
          border-color: #fff;
          color: var(--sk-color-home-primary);
        }
        .ml-btn {
          margin-left: 4.75rem;
        }
      }
    }
  }
  .product-advantages {
    border-top: 0.0625rem solid #edebeb;
    width: 100%;
    height: 37.5rem;
    background-color: var(--sk-color-home-bgc);
    display: flex;
    justify-content: center;
    align-items: center;
    .advantages {
      width: 69.25rem;
      height: 25rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .img {
        width: 44%;
        border-radius: 0.25rem;
        box-shadow: var(--sk-box-show);
        > img {
          width: 100%;
          height: 100%;
        }
      }
      .superior-panel {
        padding: 1.125rem 1.5rem;
        width: 37.5rem;
        height: 25rem;
        display: flex;
        flex-direction: column;
        align-items: flex-start;

        .introduction {
          width: 100%;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          align-items: flex-start;

          .title {
            font-size: 2rem;
            font-weight: 700;
            text-align: left;
            line-height: 1.5;
            margin-bottom: 2.5rem;
          }

          .desc {
            font-size: 1.125rem;
            font-weight: 400;
            text-align: left;
            line-height: 1.5;
            margin-bottom: 2.5rem;
            color: var(--el-color-info);
          }
        }

        .to-use {
          width: auto;
          height: 2rem;
          display: flex;
          align-items: center;
          color: var(--sk-color-home-primary);
          cursor: pointer;
          .txt {
            font-size: 1.125rem;
            font-weight: 400;
            line-height: 1.5;
            cursor: pointer;
          }
          .icon {
            cursor: pointer;
          }
        }
      }
    }
    .left {
      flex-direction: reverse;
    }
    .right {
      flex-direction: row-reverse;
    }
  }
  .quick-use {
    height: 20rem;
    background-image: url("~/assets/home/bg2.png");
    background-color: #f89126;
    background-repeat: no-repeat;
    background-size: auto 100%;
    background-position: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    > button {
      background: #fff;
      border-color: #fff;
      color: var(--sk-color-home-primary);
    }
    .title {
      font-size: 2rem;
      font-weight: 700;
      text-align: center;
      line-height: 1.5;
      margin-bottom: 2.5rem;
      color: var(--sk-color-font-white);
    }
  }

  @media screen and (min-device-width: 100rem) {
    .product-advantages {
      height: calc(37.5rem * 1.2);
      .advantages {
        width: calc(69.25rem * 1.2);
      }
    }
  }
  .product-advantages-bgc {
    background-color: var(--sk-color-cark-bgc);
  }
  .product-resources {
    height: 44.5rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--sk-color-home-primary);
    .title {
      font-size: 2rem;
      margin-top: 5rem;
      color: var(--sk-color-font-white);
    }
    .desc {
      margin-top: 2rem;
      color: var(--sk-color-font-white);
      opacity: 0.8;
    }
    .img-row {
      height: 100%;
      display: flex;
      justify-content: center;
      flex-wrap: wrap;
      align-content: flex-start;
      margin-top: 1.25rem;
      background-image: url("~/assets/home/kuxun.png");
      background-repeat: no-repeat;
      background-size: 100% auto;
    }
  }
  .border-bottom-br {
    border-bottom: 0.0625rem solid #dcdcdc;
  }
}

@media screen and (max-width: 475px) {
  .main {
    .one-word-desc-product {
      position: relative;
      padding-top: 12vh;
      flex-direction: column;
      #v1 {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        z-index: -1;
      }
      .top {
        height: calc(100vh - 20vh);
        flex: 1;
        display: flex;
        align-items: center;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
        .one-word {
          font-size: 3.4375rem;
          font-weight: 700;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 5vh;
        }
        .one-desc {
          font-size: 1.1875rem;
          font-weight: 400;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 4vh;
          color: var(--el-color-info);
        }
        .fast-use {
          font-size: 1.5rem;
          font-weight: 400;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 4vh;
        }
        .product-img {
          z-index: 9;
          position: absolute;
          bottom: 10vh;
          display: flex;
          justify-content: center;
          > img {
            width: 100%;
            height: 58vh;
            box-shadow: var(--sk-box-show);
            border-radius: 0.25rem;
          }
        }
      }
      .bottom {
        min-height: 20vh;
        width: 100%;
        background-image: url("~/assets/home/bg0.png");
        background-repeat: no-repeat;
        background-size: 100% auto;
        display: flex;
        flex-direction: column-reverse;
        .opt-detail {
          display: flex;
          justify-content: center;
          margin-bottom: 3vh;
          > button {
            background: #fff;
            border-color: #fff;
            color: var(--sk-color-home-primary);
          }
          .ml-btn {
            margin-left: 4.75rem;
          }
        }
      }
    }
    .product-advantages {
      border-top: 0.0625rem solid #edebeb;
      width: 100%;
      height: 37.5rem;
      background-color: var(--sk-color-home-bgc);
      display: flex;
      justify-content: center;
      align-items: center;
      .advantages {
        width: 100%;
        height: 25rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .img {
          width: 44%;
          border-radius: 0.25rem;
          box-shadow: var(--sk-box-show);
          > img {
            width: 100%;
            height: 100%;
          }
        }
        .superior-panel {
          padding: 1.125rem 1.5rem;
          width: 37.5rem;
          height: 25rem;
          display: flex;
          flex-direction: column;
          align-items: flex-start;

          .introduction {
            width: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            align-items: flex-start;

            .title {
              font-size: 2rem;
              font-weight: 700;
              text-align: left;
              line-height: 1.5;
              margin-bottom: 2.5rem;
            }

            .desc {
              font-size: 1.125rem;
              font-weight: 400;
              text-align: left;
              line-height: 1.5;
              margin-bottom: 2.5rem;
              color: var(--el-color-info);
            }
          }

          .to-use {
            width: auto;
            height: 2rem;
            display: flex;
            align-items: center;
            color: var(--sk-color-home-primary);
            cursor: pointer;
            .txt {
              font-size: 1.125rem;
              font-weight: 400;
              line-height: 1.5;
              cursor: pointer;
            }
            .icon {
              cursor: pointer;
            }
          }
        }
      }
      .left {
        flex-direction: reverse;
      }
      .right {
        flex-direction: row-reverse;
      }
    }
    .quick-use {
      height: 20rem;
      background-image: url("~/assets/home/bg2.png");
      background-color: #f89126;
      background-repeat: no-repeat;
      background-size: auto 100%;
      background-position: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      > button {
        background: #fff;
        border-color: #fff;
        color: var(--sk-color-home-primary);
      }
      .title {
        font-size: 2rem;
        font-weight: 700;
        text-align: center;
        line-height: 1.5;
        margin-bottom: 2.5rem;
        color: var(--sk-color-font-white);
      }
    }

    @media screen and (min-device-width: 100rem) {
      .product-advantages {
        height: calc(37.5rem * 1.2);
        .advantages {
          width: calc(69.25rem * 1.2);
        }
      }
    }
    .product-advantages-bgc {
      background-color: var(--sk-color-cark-bgc);
    }
    .product-resources {
      height: 44.5rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      background-color: var(--sk-color-home-primary);
      .title {
        font-size: 2rem;
        margin-top: 5rem;
        color: var(--sk-color-font-white);
      }
      .desc {
        margin-top: 2rem;
        color: var(--sk-color-font-white);
        opacity: 0.8;
      }
      .img-row {
        height: 100%;
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        align-content: flex-start;
        margin-top: 1.25rem;
        background-image: url("~/assets/home/kuxun.png");
        background-repeat: no-repeat;
        background-size: 100% auto;
      }
    }
    .border-bottom-br {
      border-bottom: 0.0625rem solid #dcdcdc;
    }
  }
}

#code_block {
  background-color: #fff;
  border: #e25a1b 0.0625rem solid;
  color: rgb(201, 209, 217);
  font-family: Consolas;
  font-weight: lighter;
  text-align: left;
  padding: 0.25rem;
  font-size: 1.125rem;
  padding-left: 0.875rem;
  padding-right: 0.875rem;
  border-radius: 0.3125rem;
  counter-reset: line;
  white-space: pre;
  word-spacing: normal;
  word-break: normal;
  word-wrap: normal;
  line-height: 1.8;
  .copy-icon {
    display: inline-block;
    margin-bottom: 0.125rem;
    margin-left: 0.5rem;
    font-size: 1rem;
    cursor: pointer;
    color: #e25a1b;
  }
}
</style>
