<template>
  <main class="docs">
    <!-- 导航 -->
    <nav class="left-side">
      <!-- 滚动 -->
      <el-scrollbar height="100%">
        <ContentNavigation class="nav" v-slot="{ navigation }">
          <!-- <el-tree
            default-expand-all
            :data="processMenuData(navigation)"
            :props="defaultProps"
            @node-click="handleMenuItemClick"
          /> -->
          <NavTree
            :treeData="processMenuData(navigation)"
            @item-click="handleMenuItemClick"
          ></NavTree>
        </ContentNavigation>
      </el-scrollbar>
    </nav>

    <article class="doc-content">
      <!-- 文档内容 -->
      <ContentRenderer :value="data">
        <ContentRendererMarkdown class="markdown-body" :value="data" />
        <template #empty>
          <p>No content found.</p>
        </template>
      </ContentRenderer>
    </article>
  </main>
</template>

<script setup lang="ts">
import { NavItem } from "@nuxt/content";
definePageMeta({
  title: "首页",
  layout: "home",
  keepalive: true,
});
const { params } = useRoute();
const { data, pending, error, refresh } = await useAsyncData("docs", () =>
  queryContent("/" + params.slug.join("/")).findOne()
);

const defaultProps = {
  children: "children",
  label: "title",
};

let menuData: Array<NavItem> = reactive([]);
// mounted
onMounted(() => {
  const menuDataFormSS = window.sessionStorage.getItem("menuData");
  if (menuDataFormSS) {
    // 清空 data
    menuData.splice(0, menuData.length);
    // 将 sessionStorage 中的 menuData 赋值给 data
    menuData.push(...JSON.parse(menuDataFormSS));
  }
  window.onbeforeunload = () => {
    window.sessionStorage.setItem("menuData", JSON.stringify(menuData));
  };
});

// 离开当前页面时
onBeforeUnmount(() => {
  console.log("onBeforeUnmount");
});
// 定义一个变量，用于存储menuData
function processMenuData(data: Array<NavItem>) {
  data = data.map((item) => {
    if (item.children) {
      return item.children;
    }
    return item;
  });
  // 扁平第一层
  data = data.flat();

  // 深度遍历整个data 数据，给_path 添加 /docs 前缀
  function deep(data: Array<NavItem>) {
    data.forEach((item) => {
      if (item._path.startsWith("/docs")) {
        return;
      }
      item._path = "/docs" + item._path;
      if (item.children) {
        deep(item.children);
      }
    });
  }
  deep(data);
  menuData = data;
  return data;
}

function handleMenuItemClick(link: NavItem) {
  console.log(link, "link");

  const router = useRouter();
  // 跳转到path
  router.push(link._path);
}
</script>

<style scoped lang="scss">
.docs {
  max-width: 1400px;
  margin: 20px auto;
  margin-top: 72px;
  display: flex;
  .left-side {
    position: fixed;
    top: 0;
    bottom: 0;
    margin-top: 80px;
    transition: padding-top 0.3s;
    .menu-tree {
      border-right: 2px solid #ebebeb;
    }
  }
  .doc-content {
    margin-left: 240px;
    flex: 1;
    padding: 20px;
    background-color: #fff;
  }
}
</style>
