<template>
  <header ref="headerRef" class="home-header">
    <div class="content">
      <div class="right">
        <div @click="handleLogoClick" class="home-header-logo">
          <div class="logo">
            <img src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/zhiqingyun-logo.jpg" alt=""/>
            <h1>至爻数据</h1>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="menu">
          <div
            class="menu-item"
            v-for="(item, index) in menuData"
            @click="handleMenuClick(item)"
            :key="index"
          >
            <SvgIcon v-if="item.icon" :name="item.icon" class="icon-btn">
            </SvgIcon>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
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
});

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
    headerRef.value!.style.backgroundColor = "rgba(255,255,255,0.3)";
    headerRef.value!.style.backdropFilter = "blur(10px)";

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
  height: 80px;
  display: flex;
  align-items: center;
  background-color: transparent;

  .content {
    width: 1220px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .right {
      flex: 0;
      flex-basis: 220px;
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
        width: 200px;
        height: 54px;
        display: flex;
        flex-direction: row;
        align-items: center;

        > img {
          height: 38px;
          margin-bottom: 2px;
        }

        h1 {
          padding-left: 18px;
          font-size: 24px;
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
        padding: 0px 8px;
        height: 32px;
        line-height: 32px;
        text-align: center;
        border-radius: 20px;
        margin-left: 6px;
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
          color: black;
          width: 30px;
          height: 30px;
        }
      }
    }

    // .quick-use {
    //   width: auto;
    //   padding: 0 12px;
    //   height: 32px;
    //   line-height: 32px;
    //   text-align: center;
    //   border-radius: 20px;
    //   margin-left: 14px;
    //   cursor: pointer;
    //   transition: all 0.3s;
    //   color: var(--sk-color-font-gray);
    //   border: 1px solid var(--sk-color-font-gray);
    //   &:hover {
    //     color: var(--sk-color-font-gray-hover);
    //     border: 1px solid var(--sk-color-font-gray-hover);
    //   }
    // }
    .left {
      display: flex;
      align-items: center;
    }

    .lang-change {
      margin-left: 12px;
      color: var(--sk-color-font-gray);

      &:hover {
        color: var(--sk-color-font-gray-hover);
      }
    }
  }

  @font-face {
    font-family: "阿里妈妈数黑体 Bold";
    font-weight: 700;
    src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/5QDL7m0TZ7N4.woff2") format("woff2"),
    url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/q3FAWdnOd36Q.woff") format("woff");
    font-display: swap;
  }

}

// -------------------------------------------------------------------- 移动端 ----------------------------------------------

@media (max-width: 768px) {

  .home-header {
    width: 100%;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 999;
    transition: all 0.3s;
    height: 80px;
    display: flex;
    align-items: center;
    background-color: transparent;

    .content {
      width: 300px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .right {
        flex: 0;
        flex-basis: 220px;
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
          width: 200px;
          height: 54px;
          display: flex;
          flex-direction: row;
          align-items: center;

          > img {
            height: 38px;
            margin-bottom: 2px;
          }

          h1 {
            padding-left: 18px;
            font-size: 24px;
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
          padding: 0px 8px;
          height: 32px;
          line-height: 32px;
          text-align: center;
          border-radius: 20px;
          margin-left: 6px;
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
            color: black;
            width: 30px;
            height: 30px;
          }
        }
      }

      .left {
        display: flex;
        align-items: center;
      }

      .lang-change {
        margin-left: 12px;
        color: var(--sk-color-font-gray);

        &:hover {
          color: var(--sk-color-font-gray-hover);
        }
      }
    }

    @font-face {
      font-family: "阿里妈妈数黑体 Bold";
      font-weight: 700;
      src: url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/5QDL7m0TZ7N4.woff2") format("woff2"),
      url("//at.alicdn.com/wf/webfont/aQ1mhUp3iaYf/q3FAWdnOd36Q.woff") format("woff");
      font-display: swap;
    }

  }
}
</style>
