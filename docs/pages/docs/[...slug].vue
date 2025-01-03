<template>
  <main class="docs">
    <nav class="left-side">
      <NScrollbar class="scroll-bar" height="100%" :size="4" ref="scrollbarRef">
        <ContentNavigation
          :key="docsMenuKey"
          class="nav"
          v-slot="{ navigation }"
        >
          <DocsMenuNode
            class="nav-tree"
            :treeData="processMenuData(navigation)"
            @item-click="handleMenuItemClick"
          ></DocsMenuNode>
        </ContentNavigation>
      </NScrollbar>
    </nav>
    <article class="doc-content">
      <div class="content">
        <ContentRenderer :value="data">
          <ContentRendererMarkdown
            ref="markdownBodyRef"
            class="markdown-content markdown-body"
            :value="data"
          />
          <template #empty>
            <p><a href="https://zhiqingyun.isxcode.com" style="color: #e25a1b">点击跳转官网</a></p>
          </template>
        </ContentRenderer>
      </div>
    </article>
  </main>
</template>

<script setup lang="ts">
import getContentDirTree from "~/util/getContentDirTree";
import { useCounterStore, useMenuStore } from "~/store/index";
import { NScrollbar } from "naive-ui";
import mediumZoom from "medium-zoom";

interface NavItem {
  title: string;
  _path: string;
  _id?: string;
  _draft?: boolean;
  isCollapsed?: boolean;
  level?: number;
  children?: NavItem[];
  [key: string]: any;
}

definePageMeta({
  layout: "docs",
});

const currentDoc = ref<NavItem | null>(null);
useHead({
  // title: "至轻云" + currentDoc.value?.title,
  title: "至轻云",
});

const { params } = useRoute();
const { locale } = useI18n();
const { data, pending, error, refresh } = await useAsyncData("docs", () =>
  queryContent(`/` + params.slug.join("/")).findOne()
);

const toc = ref<NavItem[]>([]);
const markdownBodyRef = ref<null>(null);
const docsMenuKey = ref(1);

onMounted(() => {
  init();
});

onMounted(async () => {
  await nextTick();
  const images = markdownBodyRef.value?.$el.querySelectorAll("img");
  if (!images) return;
  images.forEach((img: HTMLImageElement) => {
    img.classList.add("image-zoom");
  });
  mediumZoom(document.querySelectorAll(".image-zoom"), {
    margin: 100,
    scrollOffset: 1,
    background: "#fffaf8",
  });
});

const router = useRouter();
watch(
  () => router.currentRoute.value.path,
  () => {
    const pathSegments = router.currentRoute.value.path.split("/");
    const [, firstPath, , thirdPath, ...restPaths] = pathSegments;
    if (
      firstPath &&
      thirdPath &&
      firstPath === locale.value &&
      thirdPath !== firstPath
    ) {
      const newPath = `/${locale.value}/docs/${locale.value}/${restPaths.join(
        "/"
      )}`;
      router.push(newPath);
    }
  }
);

watch(
  () => router.currentRoute.value.path,
  () => {
    const { from } = router.currentRoute.value.query;
    if (from === "home") {
      setTimeout(() => {
        docsMenuKey.value++;
      }, 100);
    }
  },
  {
    immediate: true,
  }
);

function init() {
  const { height } = useCounterStore();
  if (scrollbarRef.value?.$el?.nextElementSibling) {
    const scrollElement = scrollbarRef.value.$el.nextElementSibling
      .firstChild as HTMLElement;
    if (scrollElement) {
      scrollElement.scrollTop = height;
    }
  }
  const htmlStr = markdownBodyRef.value?.$el.innerHTML || "";
  toc.value = getContentDirTree(htmlStr);
}

function updatePathDeep(navItems: Array<NavItem>, parentPath = "") {
  navItems.forEach((item) => {
    if (item.children) {
      updatePathDeep(item.children, parentPath);
    }
    if (!item._path.startsWith(parentPath)) {
      item._path = parentPath + item._path;
    }
  });
}

function processMenuData(menuData: Array<NavItem>) {
  const { menuList, setMenuList } = useMenuStore();
  const currentPath = router.currentRoute.value.path;
  if (menuList.length > 0) {
    const flag = menuList.some((item) =>
      item._path.startsWith(`/${locale.value}/`)
    );
    if (flag) {
      expandPath(menuList, currentPath);
      return menuList;
    }
  }

  if (!menuData) {
    return [];
  }
  const currentLang = locale.value;
  const targetData = menuData.find(
    (item) => item._path.slice(1) === currentLang
  );

  if (!targetData || !targetData.children) {
    return [];
  }

  menuData = targetData.children.flat();
  updatePathDeep(menuData, `/${locale.value}/docs`);

  // 合并过滤和添加 level 字段的操作
  menuData = filterAndAddLevel(menuData);

  expandPath(menuData, currentPath);

  setMenuList(menuData);
  return menuList;
}

function expandPath(menuData: Array<NavItem>, currentPath: string) {
  const path = findPathToCurrent(menuData, currentPath);
  path.forEach((item) => {
    item.isCollapsed = false;
  });
  // 最后一个节点不折叠，且保存在 currentDoc 中
  if (path.length > 0) {
    path[path.length - 1].isCollapsed = false;
    currentDoc.value = path[path.length - 1];
  }
}

function filterAndAddLevel(data: Array<NavItem>, level = 1): Array<NavItem> {
  return data.map((item) => {
    const itemTitle = item.title;
    if (item.children) {
      item.children = item.children.filter(
        (child: NavItem) => child.title !== itemTitle
      );
      item.children = filterAndAddLevel(item.children, level + 1);
    }
    return {
      ...item,
      level,
      isCollapsed: item.isCollapsed === undefined ? false : item.isCollapsed,
    };
  });
}

function findPathToCurrent(
  nodes: NavItem[],
  targetPath: string,
  ancestors: NavItem[] = []
): NavItem[] {
  for (const node of nodes) {
    if (node._path === targetPath) {
      return [...ancestors, node];
    }
    if (node.children && node.children.length > 0) {
      const result = findPathToCurrent(node.children, targetPath, [
        ...ancestors,
        node,
      ]);
      if (result.length > 0) {
        return result;
      }
    }
  }
  return [];
}

const scrollbarRef = ref<HTMLElement | null>(null);
const scrollBarScrollTop = ref(0);
const { setHeightState } = useCounterStore();

function handleMenuItemClick(link: NavItem) {
  scrollBarScrollTop.value =
    scrollbarRef.value?.$el.nextElementSibling.firstChild.scrollTop;
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
  toc.value.forEach((item) => {
    resetNodeActiveStatus(item);
  });
  node.isActive = node.isActive ? false : true;

  const markdownBody = markdownBodyRef.value.$el;
  const HList = markdownBody.querySelectorAll(`h${node.hLevel}`);
  const H = Array.from(HList).find((item) => item.innerText === node.title);
  scrollTo(H);
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
  width: 1200px;
  height: 100vh;
  padding-top: 80px;
  margin: auto;
  .left-side {
    margin-top: 80px;
    background: white;
    width: 250px;
    position: fixed;
    top: 0;
    bottom: 0;
    border-right: 1px solid #ebebeb;

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
    margin-left: 350px;
    box-sizing: border-box;
    display: flex;
    flex-direction: row;

    .aside {
      flex: 0;
      flex-basis: 260px;

      .aside-wrapper {
        box-sizing: border-box;
        overflow-x: auto;
        width: 238px;
        position: sticky;
        top: 120px;

        .aside-content {
        }
      }
    }

    .content {
      flex: 1;

      .markdown-content {
        width: 810px;
        margin-top: 70px;
        height: calc(100% - 80px);
        overflow-x: hidden;
        position: fixed;
        top: 0;
        bottom: 0;
        -ms-overflow-style: none;
        overflow-y: scroll;
        scrollbar-width: thin;
      }
    }
  }
}
</style>