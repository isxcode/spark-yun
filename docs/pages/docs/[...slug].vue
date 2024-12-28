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
            <p>No content found.</p>
          </template>
        </ContentRenderer>
      </div>
    </article>
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
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
  title: "至轻云",
});

const route = useRoute();
const router = useRouter();
const { locale } = useI18n();
const { params } = route;

const { data, pending, error, refresh } = await useAsyncData(
  "docs",
  async () => {
    const slugPath = Array.isArray(params.slug)
      ? params.slug.join("/")
      : params.slug;
    const path = slugPath.startsWith("/") ? slugPath : `/${slugPath}`;
    console.log("path", path);
    try {
      const doc = await queryContent(path).findOne();
      if (!doc) {
        console.error(`这个路径未找到资源: ${path}`);
      }
      return doc;
    } catch (err) {
      console.error(`获取资源失败: ${err}`);
      return null;
    }
  },
  {
    watch: [() => params.slug],
    server: true,
    immediate: true,
  }
);

if (!data.value) {
  handleGuideClick();
}

const toc = ref<NavItem[]>([]);
const markdownBodyRef = ref<null>(null);
const docsMenuKey = ref(1);
const scrollbarRef = ref<HTMLElement | null>(null);
const scrollBarScrollTop = ref(0);
const { setHeightState } = useCounterStore();

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
  const scrollElement = scrollbarRef.value?.$el?.nextElementSibling
    ?.firstChild as HTMLElement;
  if (scrollElement) {
    scrollElement.scrollTop = height;
  }
  const htmlStr = markdownBodyRef.value?.$el.innerHTML || "";
  toc.value = getContentDirTree(htmlStr);
}

function updatePathDeep(navItems: NavItem[], parentPath = "") {
  navItems.forEach((item) => {
    if (item.children) {
      updatePathDeep(item.children, parentPath);
    }
    if (!item._path.startsWith(parentPath)) {
      item._path = `${parentPath}${item._path}`;
    }
  });
}

function processMenuData(menuData: NavItem[]) {
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

  if (!menuData) return [];

  const targetData = menuData.find(
    (item) => item._path.slice(1) === locale.value
  );
  if (!targetData?.children) return [];

  let flatMenuData = targetData.children.flat();
  updatePathDeep(flatMenuData, `/${locale.value}/docs`);
  flatMenuData = filterAndAddLevel(flatMenuData);
  expandPath(flatMenuData, currentPath);
  setMenuList(flatMenuData);

  return menuList;
}

function expandPath(menuData: NavItem[], currentPath: string) {
  const path = findPathToCurrent(menuData, currentPath);
  path.forEach((item) => {
    item.isCollapsed = false;
  });
  if (path.length > 0) {
    path[path.length - 1].isCollapsed = false;
    currentDoc.value = path[path.length - 1];
  }
}

function filterAndAddLevel(data: NavItem[], level = 1): NavItem[] {
  return data.map((item) => {
    if (item.children) {
      item.children = item.children.filter(
        (child) => child.title !== item.title
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
      if (result.length > 0) return result;
    }
  }
  return [];
}

function handleMenuItemClick(link: NavItem) {
  scrollBarScrollTop.value =
    scrollbarRef.value?.$el?.nextElementSibling?.firstChild.scrollTop || 0;
  setHeightState(scrollBarScrollTop.value);
  router.push(link._path);
}

const resetNodeActiveStatus = (node: NavItem) => {
  node.isActive = false;
  node.children?.forEach(resetNodeActiveStatus);
};

function handleTocItemClick(node: NavItem) {
  toc.value.forEach(resetNodeActiveStatus);
  node.isActive = !node.isActive;

  const markdownBody = markdownBodyRef.value?.$el;
  const targetHeader = Array.from(
    markdownBody?.querySelectorAll(`h${node.level}`) || []
  ).find((item) => item.innerText === node.title);

  if (targetHeader) scrollTo(targetHeader);

  router.push({
    path: router.currentRoute.value.path,
    query: { anchor: node.title },
  });
}

function scrollTo(element: HTMLElement, headerOffset = 80) {
  const elementPosition = element.getBoundingClientRect().top;
  const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

  window.scrollTo({
    top: offsetPosition,
    behavior: "smooth",
  });
}

function handleGuideClick() {
  const langPrefix = locale.value;
  router.push(`/${langPrefix}/docs/${langPrefix}/0/0`);
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
