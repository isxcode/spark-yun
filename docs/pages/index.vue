<template>
  <div class="main">
    <!-- 一句话产品 -->
    <div class="one-word-desc-product">
      <div class="content">
        <div class="top">
          <div class="one-word">超轻量级大数据平台</div>
          <div class="one-desc">
            基于Spark打造企业级、超轻量、批处理大数据平台，Docker云原生一键部署，Github永久开源免费。
          </div>
          <div class="fast-use" id="code_block">
            <span class="line" line="1">
              <span style="color: #E25A1B">docker</span>
              <span style="color: #E25A1B"> run</span>
              <span style="color: #E25A1B"> -p</span>
              <span style="color: #E25A1B"> 8080</span>
              <span style="color: #E25A1B">:8080</span>
              <span style="color: #E25A1B"> -d</span>
              <span style="color: #E25A1B"> isxcode/zhiqingyun </span></span
            >
          </div>
          <div class="product-img">
            <img
              src="https://img.isxcode.com/picgo/20231102213942.png"
              alt=""
            />
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
      <div class="title">海量数据处理示例</div>
      <div class="desc">
        数十套处理某个方向数据的示例资源，即拿即用，快速开始
      </div>
      <div class="img-row">
        <div
          class="resource-item"
          v-for="(item, index) in resourceList"
          :key="index"
          @click="handleResourcesImgClick(item)"
        >
          <img :src="item.imgUrl" alt="" />
        </div>
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
            <img
              :src="serviceItem.img"
              alt=""
            />
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
    <!-- <div class="quick-use">立即开始</div> -->
    <LayoutHomeFooter />
  </div>
</template>

<script lang="ts" setup>
definePageMeta({
  title: "首页",
  layout: "home",
});

const productAdvantagesList = reactive([
  {
    code: "1",
    layout: "left",
    title: "多平台部署，从不娇生惯养",
    desc: "支持大量计算引擎平台，如Kubernetes、Spark Standalone、Spark Cluster、Yarn、CDH。可无缝迁移，无缝切换，帮助企业更优管理集群资源。",
    img: "https://img.isxcode.com/picgo/zhiqingyun1.jpg",
    url: "https://zhiqingyun-demo.isxcode.com"
  },
  {
    code: "2",
    layout: "right",
    title: "数据源驱动自定义，版本兼容问题",
    desc: "通过人工智能辅助编写和即时查询 带来最佳的coding体验",
    img: "https://img.isxcode.com/picgo/20231102214455.png",
    url: "https://zhiqingyun-demo.isxcode.com"
  },
  {
    code: "3",
    layout: "left",
    title: "连接到所有数据库",
    desc: "选择Apache Hive、 Apache Impala 、 Presto Apache Flink SQL、 SparkSQL、 Apache Phoenix、 ksqlDB、 Elastic Search、 Apache Druid 、 PostgreSQL 、 Redshift 、 BigQuery 的多个解释器之一 ...",
    img: "https://img.isxcode.com/picgo/20231102214652.png",
    url: "https://zhiqingyun-demo.isxcode.com"
  },
]);

function handleAdvantageToUse(item: { url: string|URL|undefined; }) {
  window.open(item.url);
}

const resourceList = reactive([
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215123.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215233.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215311.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215354.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215506.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215606.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215711.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215748.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102215946.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    imgUrl: "https://img.isxcode.com/picgo/20231102220038.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
]);

function  handleResourcesImgClick(item: { url: string|URL|undefined; }) {
  window.open(item.url)
}

const productServiceList = reactive([
  {
    code: "1",
    layout: "right",
    title: "适用各种操作系统，浏览器打开即用",
    desc: "适用各种操作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。作系统（MacOS Windows Linux），打开浏览器即用。",
    img: "https://img.isxcode.com/picgo/20231102214652.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  },
  {
    code: "2",
    layout: "left",
    title: "像专业人士一样编写 SQL",
    desc: "通过人工智能辅助编写和即时查询 带来最佳的coding体验",
    img: "https://img.isxcode.com/picgo/20231102214855.png",
    url: "https://zhiqingyun-demo.isxcode.com",
  }
]);

function handleServiceToUse(serviceItem: { url: string|URL|undefined; }) {
  window.open(serviceItem.url)

}
</script>

<style lang="scss" scope>
.main {
  .one-word-desc-product {
    background-color: var(--sk-color-home-bgc);
    padding-top: 126px;
    .content {
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
          font-size: 55px;
          font-weight: 700;
          text-align: center;
          line-height: 1.5;
          margin-bottom: 40px;
        }

        .one-desc {
          font-size: 19px;
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
      width: 1108px;
      height: 400px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .img {
        width: 400px;
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
      width: 1362px;
      height: 100%;
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
  .border-bottom-br {
    border-bottom: 1px solid #dcdcdc;

  }
}

#code_block {
  border: #E25A1B 1px solid;
  color: rgb(201, 209, 217);
  font-family: Consolas;
  font-weight: lighter;
  text-align: left;
  padding: 4px;
  font-size: 18px;
  padding-left: 14px;
  padding-right: 14px;
  border-radius: 5px;
  counter-reset: line;
  white-space: pre;
  word-spacing: normal;
  word-break: normal;
  word-wrap: normal;
  line-height: 1.8;
}
</style>
