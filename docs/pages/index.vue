<template>
  <div class="main">
    <!-- 一句话产品 -->
    <div class="one-word-desc-product">
      <div class="content">
        <div class="top">
          <div class="one-word">基于Spark计算引擎，轻量级大数据平台</div>
          <div class="one-desc">
            我们的基于Spark的超轻量级大数据平台，高效处理和灵活扩展，帮助企业快速决策，提升竞争力
          </div>
          <div class="fast-use">五分钟快速上手</div>
          <div class="product-img">
            <img src="~assets/images/初音.png" alt="" />
          </div>
        </div>
        <div class="bottom"></div>
      </div>
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
          <img src="~assets/images/初音.png" alt="" />
        </div>
        <div class="superior-panel">
          <div class="introduction">
            <div class="title">{{ advantageItem.title }}</div>
            <div class="desc">
              {{ advantageItem.desc }}
            </div>
          </div>
          <div class="to-use">
            <span class="txt"> 快速使用 &nbsp; </span>
            <el-icon class="icon"><DArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>
    <!-- 产品资源 -->
    <div class="product-resources">
      <div class="title">海量数据处理示例</div>
      <div class="desc">
        数十套处理某个方向数据的示例资源，即拿即用，快速开始
      </div>
      <div class="img-row">
        <div
          class="resource-item"
          v-for="(item, index) in resourceList"
          :key="index"
        >
          <img :src="item.imgUrl" alt="" />
        </div>
      </div>
    </div>
    <!-- 产品服务 -->
    <div class="product-service">
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
            <img src="~assets/images/初音.png" alt="" />
          </div>
          <div class="superior-panel">
            <div class="introduction">
              <div class="title">{{ serviceItem.title }}</div>
              <div class="desc">
                {{ serviceItem.desc }}
              </div>
            </div>
            <div class="to-use">
              <span class="txt"> 快速使用 &nbsp; </span>
              <el-icon class="icon"><DArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 号召使用 -->
    <div class="quick-use">立即开始</div>
    <!-- 底部栏 -->
    <LayoutHomeFooter />
  </div>
</template>

<script lang="ts" setup>
definePageMeta({
  title: "首页",
  layout: "home",
});
import { DArrowRight } from "@element-plus/icons-vue";

const productAdvantagesList = reactive([
  {
    code: "1",
    layout: "left",
    title: "适用各种操作系统，浏览器打开即用",
    desc: "适用各种操作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。",
    img: "https://img.js.design/assets/img/648150cf101effe1f9fe8f3f.png",
  },
  {
    code: "2",
    layout: "right",
    title: "像专业人士一样编写 SQL",
    desc: "通过人工智能辅助编写和即时查询 带来最佳的coding体验",
    img: "https://img.js.design/assets/img/648150cf101effe1f9fe8f3f.png",
  },
  {
    code: "3",
    layout: "left",
    title: "连接到所有数据库",
    desc: "选择Apache Hive、 Apache Impala 、 Presto Apache Flink SQL、 SparkSQL、 Apache Phoenix、 ksqlDB、 Elastic Search、 Apache Druid 、 PostgreSQL 、 Redshift 、 BigQuery 的多个解释器之一 ...",
  },
]);

const resourceList = reactive([
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
  {
    imgUrl: "/_nuxt/assets/images/初音.png",
    url: "",
  },
]);

const productServiceList = reactive([
  {
    code: "1",
    layout: "right",
    title: "适用各种操作系统，浏览器打开即用",
    desc: "适用各种操作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。",
    img: "https://img.js.design/assets/img/648150cf101effe1f9fe8f3f.png",
  },
  {
    code: "2",
    layout: "left",
    title: "像专业人士一样编写 SQL",
    desc: "通过人工智能辅助编写和即时查询 带来最佳的coding体验",
    img: "https://img.js.design/assets/img/648150cf101effe1f9fe8f3f.png",
  },
]);
</script>

<style lang="scss" scope>
.main {
  .one-word-desc-product {
    background-color: var(--sk-color-home-bgc);
    &::before {
      // z-index: -9;
      // background-position: center;
      // background-size: cover;
      // background-repeat: no-repeat;
      // position: absolute;
      // top: 0;
      // left: 0;
      // right: 0;
      // bottom: 0;
      // content: "";
      // opacity: 1;
      // background-size: cover;
      // background-image: url(https://img.js.design/assets/img/648150cf101effe1f9fe8f3f.png);
    }
    .content {
      padding-top: 126px;
      .top {
        display: flex;
        justify-content: space-between;
        align-items: center;

        // 居中布局
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .one-word {
          font-size: 64px;
          font-weight: 700;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 40px;
        }

        .one-desc {
          font-size: 24px;
          font-weight: 400;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 40px;
          color: var(--el-color-info);
        }

        .fast-use {
          font-size: 24px;
          font-weight: 400;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 40px;
        }

        .product-img {
          width: 100%;
          height: 360px;
          margin-top: 30px;
          margin-bottom: 40px;
          display: flex;
          justify-content: center;

          > img {
            width: 800px;
            height: 560px;
            box-shadow: var(--sk-box-show);
            border-radius: 4px;
          }
        }
      }

      .bottom {
        width: 100%;
        height: 220px;
        background-color: var(--sk-color-home-primary);
      }
    }
  }

  .product-advantages {
    border-top: 1px solid #edebeb;
    width: 100%;
    height: 600px;
    background-color: var(--sk-color-home-bgc);
    display: flex;
    justify-content: center;
    align-items: center;

    .advantages {
      width: 1200px;
      height: 400px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .img {
        width: 400px;
        height: 400px;
        border-radius: 4px;
        box-shadow: var(--sk-box-show);

        > img {
          width: 100%;
          height: 100%;
        }
      }

      .superior-panel {
        padding: 18px 24px;
        width: 600px;
        height: 400px;
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
            font-size: 32px;
            font-weight: 700;
            text-align: left;
            line-height: 1.5;
            margin-bottom: 40px;
          }

          .desc {
            font-size: 18px;
            font-weight: 400;
            text-align: left;
            line-height: 1.5;
            margin-bottom: 40px;
            color: var(--el-color-info);
          }
        }

        .to-use {
          width: auto;
          height: 32px;
          display: flex;
          align-items: center;
          color: var(--sk-color-home-primary);
          cursor: pointer;
          .txt {
            font-size: 18px;
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
  .product-advantages-bgc {
    background-color: var(--sk-color-cark-bgc);
  }
  .product-resources {
    height: 712px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--sk-color-home-primary);
    .title {
      font-size: 32px;
      margin-top: 80px;
      color: var(--sk-color-font-white);
    }
    .desc {
      margin-top: 32px;
      color: var(--sk-color-font-white);
      // 透明度
      opacity: 0.8;
    }

    .img-row {
      height: 100%;
      width: 1600px;
      display: flex;
      justify-content: center;
      flex-wrap: wrap;
      align-content: flex-start;
      margin-top: 20px;
      .resource-item {
        width: 256px;
        height: 171.33px;
        margin-left: 20px;
        border-radius: 12px;
        // box-shadow: var(--sk-box-show);
        background-color: var(--sk-color-cark-bgc);
        > img {
          width: 100%;
          height: 100%;
          border-radius: 12px;
        }
      }
      // 每行的第一个元素 不设置margin-left
      .resource-item:nth-child(5n + 1) {
        margin-left: 0;
      }
      // 第二行开始 设置margin-top
      .resource-item:nth-child(n + 6) {
        margin-top: 20px;
      }
    }
  }
}
</style>
