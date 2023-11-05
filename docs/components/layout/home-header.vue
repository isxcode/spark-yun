<template>
  <header ref="headerRef" class="home-header">
    <div class="content">
      <div class="right">
        <div @click="handleLogoClick" class="home-header-logo">
          <div class="logo">
            <img src="~assets/images/logo.png" alt="" />
            <h1>至轻云</h1>
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
            <UIcon v-if="item.icon" :name="item.icon" class="icon" />
            <span v-if="!item.icon">
              {{ item.title }}
            </span>
          </div>
        </div>
      </div>
      <div class="left">
        <div class="quick-use" @click="handleQuickUseClick">快速试用</div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
defineComponent("LayoutHomeHeader");
const headerRef = ref<HTMLElement | null>(null);

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

function handleScroll() {
  const flag = window.location.pathname === "/";
  if (!flag) {
    headerRef.value!.style.backgroundColor = "var(--sk-color-home-bgc)";
    headerRef.value!.style.height = "60px";
    headerRef.value!.style.boxShadow = "0 2px 4px -1px rgba(0,0,0,0.25)";
    return;
  }
  if (window.scrollY > 0) {
    headerRef.value!.style.backgroundColor = "#fff";
    headerRef.value!.style.height = "60px";
    headerRef.value!.style.boxShadow = "0 2px 4px -1px rgba(0,0,0,0.25)";
  } else {
    headerRef.value!.style.height = "80px";
    headerRef.value!.style.boxShadow = "none";
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
  // {
  //   title: "帮助中心",
  //   path: "/install/docker-deploy",
  //   type: "router",
  // },
  {
    title: "github",
    icon: "i-simple-icons-github",
    path: "https://github.com/isxcode/spark-yun",
    type: "link",
  },
  {
    title: "gitee",
    icon: "i-simple-icons-gitee",
    path: "https://gitee.com/isxcode/spark-yun",
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

function handleQuickUseClick() {
  window.open("https://zhiqingyun-demo.isxcode.com");
}
</script>

<style lang="scss" scoped>
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background-color: var(--sk-color-home-bgc);
  transition: all 0.3s;
  width: 100%;
  height: 80px;
  display: flex;
  align-items: center;
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
        width: 154px;
        height: 54px;
        display: flex;
        flex-direction: row;
        align-items: center;
        > img {
          width: 32px;
          height: 32px;
          margin-bottom: 2px;
        }
        h1 {
          padding-left: 8px;
          font-size: 24px;
          font-weight: 700;
          z-index: 999;
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
        &:hover {
          color: var(--sk-color-font-gray-hover);
        }

        > .icon {
          margin-top: 8px;
        }
      }
    }
    .quick-use {
      width: auto;
      padding: 0 12px;
      height: 32px;
      line-height: 32px;
      text-align: center;
      border-radius: 20px;
      margin-left: 14px;
      cursor: pointer;
      transition: all 0.3s;
      color: var(--sk-color-font-gray);
      border: 1px solid var(--sk-color-font-gray);
      &:hover {
        color: var(--sk-color-font-gray-hover);
        border: 1px solid var(--sk-color-font-gray-hover);
      }
    }
  }
}
</style>
