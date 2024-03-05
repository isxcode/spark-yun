<template>
  <main class="docs">
    <nav class="left-side">
      <el-scrollbar
        class="scroll-bar"
        always
        height="100%"
        :scroll-bar="{
          background: 'red',
        }"
        ref="scrollbarRef"
      >
        <ContentNavigation class="nav" v-slot="{ navigation }">
          <DocsMenuNode
            class="nav-tree"
            :treeData="processMenuData(navigation)"
            @item-click="handleMenuItemClick"
          ></DocsMenuNode>
        </ContentNavigation>
      </el-scrollbar>
    </nav>
    <article class="doc-content">
      <div class="content">
        <ContentRenderer :value="data">
          <ContentRendererMarkdown
            ref="markdownBodyRef"
            class="markdown-body github-markdown-light"
            :value="data"
          />
          <template #empty>
            <p>No content found.</p>
          </template>
        </ContentRenderer>
      </div>
      <div class="aside">
        <div class="aside-wrapper">
          <div class="aside-content">
            <DocsToc
              :contentDirTree="toc"
              :activeNodeIdList="['1', '2']"
              @nodeClicked="handleTocItemClick"
            />
          </div>
        </div>
      </div>
    </article>
  </main>
</template>

<script setup lang="ts">
import { NavItem } from "@nuxt/content";
import getContentDirTree from "~/util/getCOntentDirTree";
import { useCounterStore, useMenuStore } from "~/store/index";
definePageMeta({
  title: "首页",
  layout: "home",
});
const { params } = useRoute();
const { data, pending, error, refresh } = await useAsyncData("docs", () =>
  queryContent("/" + params.slug.join("/")).findOne()
);

const markdownBodyRef = ref<HTMLElement | null>(null);
const toc = ref<NavItem[]>([]);
onMounted(() => {
  const { height } = useCounterStore();
  scrollbarRef.value.$el.querySelector(".el-scrollbar__wrap").scrollTop =
    height;
  const htmlStr = markdownBodyRef.value?.$el.innerHTML || "";
  toc.value = getContentDirTree(htmlStr);
});

function processMenuData(data: Array<NavItem>) {
  data = data.map((item) => {
    if (item.children) {
      return item.children;
    }
    return item;
  });
  data = data.flat();
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
  const { menuList, setMenuList } = useMenuStore();
  const flag = isEqual(data, menuList);
  if (flag) {
    return menuList;
  }

  // 过滤掉和父亲节点名字一致的儿子节点
  function filterIndex(data: Array<NavItem>) {
    data = data.filter((item) => {
      const itemTitle = item.title;
      if (item.children) {
        item.children = item.children.filter((child) => {
          return child.title !== itemTitle;
        });
        item.children = filterIndex(item.children);
      }
      return item;
    });
    return data;
  }
  data = filterIndex(data);
  setMenuList(data);
  return menuList;
}

const scrollbarRef = ref<HTMLElement | null>(null);
const scrollBarScrollTop = ref(0);
const { height, setHeightState } = useCounterStore();
function handleMenuItemClick(link: NavItem) {
  scrollBarScrollTop.value = scrollbarRef.value?.$el.querySelector(
    ".el-scrollbar__wrap"
  )!.scrollTop;
  setHeightState(scrollBarScrollTop.value);
  const router = useRouter();
  router.push(link._path);
}

const resetNodeActiveStatus = (node) => {
  node.isActive = false;
  if (node.children) {
    node.children.forEach((child) => {
      resetNodeActiveStatus(child);
    });
  }
};

function handleTocItemClick(node: DirNode) {
  // 设置活跃节点
  toc.value.forEach((item) => {
    resetNodeActiveStatus(item);
  });
  node.isActive = node.isActive ? false : true;

  const markdownBody = markdownBodyRef.value.$el;
  const HList = markdownBody.querySelectorAll(`h${node.hLevel}`);
  const H = Array.from(HList).find((item) => item.innerText === node.title);
  scrollTo(H);
  // 修改路由，添加锚点
  const router = useRouter();
  router.push({
    path: router.currentRoute.value.path,
    query: {
      anchor: node.title,
    },
  });
}

function scrollTo(element, headerOffset = 80) {
  const elementPosition = element.getBoundingClientRect().top;
  const offsetPosition = elementPosition + window.pageYOffset - headerOffset;
  console.log(elementPosition, window.pageYOffset, offsetPosition);

  window.scrollTo({
    top: offsetPosition,
    behavior: "smooth",
  });
}
</script>

<style scoped lang="scss">
.docs {
  margin: 1.25rem auto;
  margin-top: 4.5rem;
  .left-side {
    width: 19.375rem;
    position: fixed;
    top: 0;
    bottom: 0;
    margin-top: 5rem;
    border-right: 0.0625rem solid #ebebeb;
    .scroll-bar {
      .el-scrollbar__wrap {
        height: 100%;
        overflow: hidden;
        .el-scrollbar__view {
          height: 100%;
          overflow-y: auto;
          overflow-x: hidden;
        }
      }
    }
  }
  .doc-content {
    box-sizing: border-box;
    padding-left: 19.375rem;
    background-color: #fff;
    display: flex;
    flex-direction: row;
    .aside {
      flex: 0;
      flex-basis: 16.25rem;
      .aside-wrapper {
        box-sizing: border-box;
        overflow-x: auto;
        width: 14.875rem;
        position: sticky;
        top: 7.5rem;
        .aside-content {
        }
      }
    }
    .content {
      flex: 1;
      padding: 4rem 0 6rem 4.125rem;
      .markdown-body {
        width: calc(100vw - 19.375rem - 4.125rem - 16.25rem);
      }
    }
  }
}
</style>
