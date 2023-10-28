<template>
  <header class="sk-header">
    <div class="sk-header-logo">
      <img src="~/assets/images/favicon.png" alt="logo" />
      <h1>至轻云</h1>
    </div>
    <div class="sk-header-content">
      <a
        :key="item.target"
        v-for="item in contentList"
        class="sk-header-content-item"
        @click="handleClick(item)"
      >
        {{ item.name }}
      </a>
      <button @click="toggleDark()">
        <i inline-block align-middle i="dark:carbon-moon carbon-sun" />
        <span class="ml-2">{{ isDark ? "Dark" : "Light" }}</span>
      </button>
    </div>
    <div class="h5-options">
      <el-button @click="toggleDark()">
        <i inline-block align-middle i="dark:carbon-moon carbon-sun" />
        <span class="ml-2">{{ isDark ? "Dark" : "Light" }}</span>
      </el-button>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useDark, useToggle } from "@vueuse/core";

defineComponent({
  name: "SkHeader",
});

type contentItemType = {
  name: string;
  target: string;
  type: contentItemTypeType;
};
type contentItemTypeType = "primary" | "external_link";
type contentItemTypeHandleType = {
  [key in contentItemTypeType]: (item: contentItemType) => void;
};
const contentItemTypeHandle: contentItemTypeHandleType = {
  primary: (item: contentItemType) => {
    router.push({ path: `/${item.target}` });
  },
  external_link: (item: contentItemType) => {
    window.open(item.target);
  },
};
const contentList: contentItemType[] = reactive([
  {
    name: "产品优势",
    target: "advantage",
    type: "primary",
  },
  {
    name: "技术架构",
    target: "technology",
    type: "primary",
  },
  {
    name: "版本差异",
    target: "version",
    type: "primary",
  },
  {
    name: "应用场景",
    target: "application",
    type: "primary",
  },
  {
    name: "联系我们",
    target: "about",
    type: "primary",
  },
  {
    name: "文档中心",
    target: "guide",
    type: "primary",
  },
  {
    name: "Docker开源",
    target: "https://hub.docker.com/r/isxcode/zhiqingyun",
    type: "external_link",
  },
  {
    name: "Github开源",
    target: "https://github.com/isxcode/spark-yun",
    type: "external_link",
  },
]);

const router = useRouter();
const handleClick = (item: contentItemType) => {
  contentItemTypeHandle[item.type](item);
};

const isDark = useDark();
const toggleDark = useToggle(isDark);
</script>

<style lang="scss" scope>
.sk-header {
  position: fixed;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  box-shadow: 0 0 10px var(--el-color-info-dark-2);
  background-image: radial-gradient(transparent 1px, var(--el-bg-color) 1px);
  background-size: 4px 4px;
  backdrop-filter: saturate(50%) blur(4px);
  .sk-header-logo {
    margin-left: 32px;
    min-width: 120px;
    flex: 0;
    display: flex;
    align-items: center;
    img {
      width: 40px;
      height: 40px;
      margin-right: 10px;
    }
    h1 {
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
  .sk-header-content {
    margin-left: auto;
    margin-right: 24px;
    flex: 1;
    height: 100%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    .sk-header-content-item {
      line-height: 48px;
      margin: 0 10px;
      font-size: 14px;
      color: var(--el-text-color-primary);
      cursor: pointer;
      &:hover {
        color: var(--el-color-primary);
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .sk-header {
    .sk-header-content {
      display: none !important;
    }
  }
}
@media screen and (min-width: 768px) {
  .h5-options {
    display: none;
  }
}
</style>
