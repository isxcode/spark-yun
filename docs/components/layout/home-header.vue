<template>
  <header ref="headerRef" class="home-header">
    <div class="content">
      <div class="right">
        <div @click="handleLogoClick" class="home-header-logo">
          <div class="logo">
            <img src="~assets/images/logo.png" alt="" />
            <h1>至爻数据</h1>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="menu">
          <div class="menu-item" v-for="(item, index) in menuData" @click="handleMenuClick(item)" :key="index">
            <SvgIcon v-if="item.icon" :name="item.icon" class="icon-btn"> </SvgIcon>
          </div>
        </div>
      </div>
      <div class="left">
        <el-dropdown class="lang-change" trigger="click" :hide-on-click="true">
          <span class="el-dropdown-link">
            {{ currentLangText }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="(item, index) in langMap" :key="index" @click="handleLangChange(index)">
                {{ item }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <!-- <div class="quick-use" @click="handleQuickUseClick">中英文</div> -->
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { isMobile } from "~/util/isMobile.js";

defineComponent("LayoutHomeHeader");

// 不同的语言对应不同的显示文字
const langMap = reactive<Record<string, string>>({
  "zh-CN": "中文",
  "en-US": "English",
});
//  声明当前语言信息
const currentLang = ref<string>("zh-CN");
// 计算属性，根据当前语言信息获取对应的显示文字
const currentLangText = computed(() => {
  return langMap[currentLang.value];
});
// 语言切换
function handleLangChange(lang: string) {
  currentLang.value = lang;
}

const headerRef = ref<HTMLElement | null>(null);

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  if (isMobile()) {
    disableZoom();
  }
});

function disableZoom(): void {
  const existingViewportMeta: HTMLMetaElement | null = document.querySelector('meta[name="viewport"]');
  if (existingViewportMeta) {
    existingViewportMeta.remove();
  }

  const meta: HTMLMetaElement = document.createElement("meta");
  meta.name = "viewport";
  meta.content = "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no";
  document.getElementsByTagName("head")[0].appendChild(meta);
}

function handleScroll() {
  const flag = window.location.pathname === "/";
  if (!flag) {
    headerRef.value!.style.height = "60px";
    headerRef.value!.style.boxShadow = "0 2px 4px -1px rgba(0,0,0,0.25)";
    headerRef.value!.style.backgroundColor = "transparent";
    return;
  }
  if (window.scrollY > 0) {
    headerRef.value!.style.backgroundColor = "#fff";
    headerRef.value!.style.height = "60px";
    headerRef.value!.style.boxShadow = "0 2px 4px -1px rgba(0,0,0,0.25)";
    // 移动端设置白底
    if (isMobile()) {
      headerRef.value!.style.backgroundColor = "#fff";
    } else {
      headerRef.value!.style.backgroundColor = "rgba(255,255,255,0.3)";
      headerRef.value!.style.backdropFilter = "blur(10px)";
    }
  } else {
    headerRef.value!.style.height = "80px";
    headerRef.value!.style.boxShadow = "none";
    headerRef.value!.style.backgroundColor = "transparent";
    headerRef.value!.style.backdropFilter = "none";
  }
}

const router = useRouter();

watch(
  () => router.currentRoute.value.path,
  (path) => {
    const whiteList = ["/"];
    const flag = whiteList.some((item) => {
      const path = window.location.pathname.split("/")[1];
      return item === `/${path}`;
    });
    if (!flag) {
      headerRef.value!.style.backgroundColor = "var(--sk-color-home-bgc)";
      headerRef.value!.style.height = "60px";
      headerRef.value!.style.boxShadow = "0 2px 4px -1px rgba(0,0,0,0.25)";
      return;
    }
    if (path === "/") {
      headerRef.value!.style.height = "80px";
      headerRef.value!.style.boxShadow = "none";
    }
  }
);

// logo 点击
function handleLogoClick() {
  const router = useRouter();
  router.push("/");
}

// 菜单数据接口interface

interface MenuData {
  title: string;
  path: string;
  type: string;
  icon?: string;
}

const menuData: Array<MenuData> = reactive([
  {
    title: "github",
    icon: "github",
    path: "https://github.com/isxcode/spark-yun",
    type: "link",
  },
]);
function handleMenuClick(menuItem: MenuData) {
  if (menuItem.type === "router") {
    const router = useRouter();
    if (menuItem.path) {
      router.push(menuItem.path);
    }
  }
  if (menuItem.type === "link") {
    window.open(menuItem.path);
  }
}
</script>

<style lang="scss" scoped>
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  transition: all 0.3s;
  width: 100%;
  height: 5rem;
  display: flex;
  align-items: center;
  background-color: transparent;
  .content {
    width: 76.25rem;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .right {
      flex: 0;
      flex-basis: 13.75rem;
    }
    .center {
      flex: 1;
    }
    .left {
      flex: 0;
      flex-basis: auto;
    }
    .home-header-logo {
      cursor: pointer;
      z-index: 999;
      .logo {
        color: var(--sk-color-home-primary);
        width: 12.5rem;
        height: 3.375rem;
        display: flex;
        flex-direction: row;
        align-items: center;
        > img {
          height: 2.375rem;
          margin-bottom: 0.125rem;
        }
        h1 {
          padding-left: 0.5rem;
          font-size: 1.5rem;
          z-index: 999;
          color: #3e3e3e;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
        }
      }
    }
    .menu {
      flex: 1;
      display: flex;
      justify-content: flex-end;

      .menu-item {
        padding: 0rem 0.5rem;
        height: 2rem;
        line-height: 2rem;
        text-align: center;
        border-radius: 1.25rem;
        margin-left: 0.375rem;
        cursor: pointer;
        transition: all 0.3s;
        color: var(--sk-color-font-gray);
        display: flex;
        align-items: center;
        justify-content: center;
        &:hover {
          color: var(--sk-color-font-gray-hover);
        }

        > .icon-btn {
          width: 1.5rem;
          height: 1.5rem;
        }
      }
    }
    .left {
      display: flex;
      align-items: center;
    }
    .lang-change {
      margin-left: 0.75rem;
      color: var(--sk-color-font-gray);
      &:hover {
        color: var(--sk-color-font-gray-hover);
      }
    }
  }
  @font-face {
    font-family: "阿里妈妈数黑体 Bold";
    font-weight: 700;
    src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/5QDL7m0TZ7N4.woff2") format("woff2"), url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/q3FAWdnOd36Q.woff") format("woff");
    font-display: swap;
  }
}

@media screen and (max-width: 475px) {
  .home-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 999;
    transition: all 0.3s;
    width: 100%;
    height: 5rem;
    display: flex;
    align-items: center;
    background-color: transparent;
    .content {
      width: 100%;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .right {
        flex: 0;
        flex-basis: 13.75rem;
      }
      .center {
        flex: 1;
      }
      .left {
        flex: 0;
        flex-basis: auto;
      }
      .home-header-logo {
        cursor: pointer;
        z-index: 999;
        .logo {
          color: var(--sk-color-home-primary);
          width: 12.5rem;
          height: 3.375rem;
          display: flex;
          flex-direction: row;
          align-items: center;
          > img {
            height: 2.375rem;
            margin-bottom: 0.125rem;
          }
          h1 {
            padding-left: 0.5rem;
            font-size: 1.5rem;
            z-index: 999;
            color: #3e3e3e;
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
          }
        }
      }
      .menu {
        flex: 1;
        display: flex;
        justify-content: flex-end;

        .menu-item {
          padding: 0rem 0.5rem;
          height: 2rem;
          line-height: 2rem;
          text-align: center;
          border-radius: 1.25rem;
          margin-left: 0.375rem;
          cursor: pointer;
          transition: all 0.3s;
          color: var(--sk-color-font-gray);
          display: flex;
          align-items: center;
          justify-content: center;
          &:hover {
            color: var(--sk-color-font-gray-hover);
          }

          > .icon-btn {
            width: 1.5rem;
            height: 1.5rem;
          }
        }
      }
      .left {
        display: flex;
        align-items: center;
      }
      .lang-change {
        margin-left: 0.75rem;
        color: var(--sk-color-font-gray);
        &:hover {
          color: var(--sk-color-font-gray-hover);
        }
      }
    }
    @font-face {
      font-family: "阿里妈妈数黑体 Bold";
      font-weight: 700;
      src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/5QDL7m0TZ7N4.woff2") format("woff2"), url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/q3FAWdnOd36Q.woff") format("woff");
      font-display: swap;
    }
  }
}
</style>
