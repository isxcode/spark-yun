<template>
  <header ref="headerRef" class="home-header">
    <div class="content">
      <div class="right">
        <div @click="handleLogoClick" class="home-header-logo">
          <div class="logo">
            <img src="~assets/images/favicon.png" alt="" />
            <h1>至轻云</h1>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="menu">
          <div
            class="menu-item"
            v-for="item in menuData"
            @click="handleMenuClick(item.path)"
          >
            {{ item.title }}
          </div>
        </div>
      </div>
      <div class="left">
        <div class="quick-use">快速使用</div>
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
  const blackList = ["/docs", "/blog"];
  const flag = blackList.some((item) => {
    // 路径域名之后的内容
    const path = window.location.pathname.split("/")[1];
    return item === `/${path}`;
  });
  if (flag) {
    headerRef.value!.style.backgroundColor = "var(--sk-color-home-bgc)";
    headerRef.value!.style.height = "60px";
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

// 监听路由变化，当路由为 /docs 或者 /blog 时，header 的背景色为白色，高度固定为 60px
const router = useRouter();

watch(
  () => router.currentRoute.value.path,
  (path) => {
    const blackList = ["/docs", "/blog"];
    const flag = blackList.some((item) => {
      // 路径域名之后的内容
      const path = window.location.pathname.split("/")[1];
      return item === `/${path}`;
    });
    if (flag) {
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

const menuData = reactive([
  {
    title: "文档",
    path: "/docs",
  },
  {
    title: "博客",
    path: "/blog",
  },
  {
    title: "github",
    path: "",
  },
]);
function handleMenuClick(path: string) {
  const router = useRouter();
  if (path) {
    router.push(path);
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
  background-color: var(--sk-color-home-bgc);
  transition: all 0.3s;
  width: 100%;
  height: 80px;
  display: flex;
  align-items: center;

  .content {
    width: 1348px;
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
      // 鼠标样式
      cursor: pointer;
      // 防止被遮挡
      z-index: 999;
      .logo {
        width: 154px;
        height: 54px;
        display: flex;
        flex-direction: row;
        align-items: center;

        > img {
          width: 32px;
          height: 32px;
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
        width: 80px;
        height: 36px;
        line-height: 36px;
        text-align: center;
        border-radius: 20px;
        margin-left: 14px;
        cursor: pointer;
        transition: all 0.3s;
        color: var(--sk-color-font-gray);

        &:hover {
          color: var(--sk-color-font-gray-hover);
        }
      }
    }
    .quick-use {
      width: 100px;
      height: 40px;
      line-height: 40px;
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
