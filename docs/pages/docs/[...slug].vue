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
            <p><a style="color: #0969da; cursor: pointer;" @click="clickHome">点击刷新</a></p>
          </template>
        </ContentRenderer>
      </div>
      <aside class="toc-aside" v-if="toc.length > 0 && !isImageZoomed">
        <div class="toc-wrapper">
          <DocsToc
            :contentDirTree="toc"
            @nodeClicked="handleTocItemClick"
          />
        </div>
      </aside>
    </article>
  </main>
</template>

<script setup lang="ts">
import getContentDirTree from "~/util/getContentDirTree";
import { useCounterStore, useMenuStore } from "~/store/index";
import { NScrollbar } from "naive-ui";
import mediumZoom from "medium-zoom";
import { DirNode } from "~/interface/docs.interface";

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

const toc = ref<DirNode[]>([]);
const markdownBodyRef = ref<null>(null);
const docsMenuKey = ref(1);
const isImageZoomed = ref(false);

onMounted(() => {
  init();
  setupScrollListener();
  setupCodeCopyButtons();
});

onMounted(async () => {
  await nextTick();
  const images = markdownBodyRef.value?.$el.querySelectorAll("img");
  if (!images) return;
  images.forEach((img: HTMLImageElement) => {
    img.classList.add("image-zoom");
  });
  const zoom = mediumZoom(document.querySelectorAll(".image-zoom"), {
    margin: 100,
    scrollOffset: 1,
    background: "#fffaf8",
  });

  // 监听图片放大和关闭事件
  zoom.on('open', () => {
    isImageZoomed.value = true;
  });

  zoom.on('close', () => {
    isImageZoomed.value = false;
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

  // 延迟设置代码复制按钮，确保DOM已渲染
  nextTick(() => {
    setupCodeCopyButtons();
  });
}

function clickHome() {
  const router = useRouter();
  router.push("/");
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
  node.isActive = true;

  const markdownBody = markdownBodyRef.value.$el;

  // 首先尝试通过domId查找元素
  let targetElement = null;
  if (node.domId) {
    targetElement = markdownBody.querySelector(`#${node.domId}`);
  }

  // 如果通过domId找不到，则通过标题文本查找
  if (!targetElement) {
    const HList = markdownBody.querySelectorAll(`h${node.hLevel}`);
    targetElement = Array.from(HList).find((item) => {
      const text = item.innerText.trim();
      return text === node.title.trim();
    });
  }

  // 如果还是找不到，尝试模糊匹配
  if (!targetElement) {
    const allHeadings = markdownBody.querySelectorAll('h1, h2, h3, h4, h5, h6');
    targetElement = Array.from(allHeadings).find((item) => {
      const text = item.innerText.trim();
      return text.includes(node.title.trim()) || node.title.trim().includes(text);
    });
  }

  if (targetElement) {
    scrollTo(targetElement);

    // 更新URL hash
    const router = useRouter();
    router.push({
      path: router.currentRoute.value.path,
      hash: node.domId ? `#${node.domId}` : '',
      query: {
        anchor: node.title,
      },
    });
  } else {
    console.warn('无法找到对应的标题元素:', node.title);
  }
}

function scrollTo(element, headerOffset = 100) {
  if (!element) return;

  // 获取markdown容器
  const markdownContainer = markdownBodyRef.value?.$el;
  if (!markdownContainer) return;

  // 计算元素在容器中的位置
  const containerRect = markdownContainer.getBoundingClientRect();
  const elementRect = element.getBoundingClientRect();

  // 计算需要滚动的距离
  const scrollTop = markdownContainer.scrollTop;
  const targetScrollTop = scrollTop + (elementRect.top - containerRect.top) - headerOffset;

  // 平滑滚动到目标位置
  markdownContainer.scrollTo({
    top: Math.max(0, targetScrollTop),
    behavior: "smooth",
  });
}

function setupScrollListener() {
  const markdownContent = markdownBodyRef.value?.$el;
  if (!markdownContent) return;

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const headingText = entry.target.innerText;
          updateActiveTocItem(headingText);
        }
      });
    },
    {
      rootMargin: '-80px 0px -80% 0px',
      threshold: 0
    }
  );

  // 观察所有标题元素
  const headings = markdownContent.querySelectorAll('h1, h2, h3, h4, h5, h6');
  headings.forEach((heading) => {
    observer.observe(heading);
  });

  onUnmounted(() => {
    observer.disconnect();
  });
}

function updateActiveTocItem(headingText) {
  // 重置所有TOC项的激活状态
  toc.value.forEach((item) => {
    resetNodeActiveStatus(item);
  });

  // 查找并激活匹配的TOC项
  const findAndActivateNode = (nodes) => {
    for (const node of nodes) {
      if (node.title === headingText) {
        node.isActive = true;
        return true;
      }
      if (node.children && findAndActivateNode(node.children)) {
        return true;
      }
    }
    return false;
  };

  findAndActivateNode(toc.value);
}

function setupCodeCopyButtons() {
  const markdownContent = markdownBodyRef.value?.$el;
  if (!markdownContent) return;

  // 查找所有代码块
  const codeBlocks = markdownContent.querySelectorAll('pre code');

  codeBlocks.forEach((codeBlock, index) => {
    const pre = codeBlock.parentElement;
    if (!pre || pre.querySelector('.code-copy-btn')) return; // 避免重复添加

    // 创建复制按钮容器
    const copyContainer = document.createElement('div');
    copyContainer.className = 'code-copy-container';

    // 创建复制按钮
    const copyBtn = document.createElement('button');
    copyBtn.className = 'code-copy-btn';
    copyBtn.innerHTML = `
      <svg class="copy-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
      </svg>
      <span class="copy-text">Copy</span>
    `;

    // 添加点击事件
    copyBtn.addEventListener('click', async () => {
      try {
        const code = codeBlock.textContent || '';
        await navigator.clipboard.writeText(code);

        // 显示复制成功状态
        copyBtn.innerHTML = `
          <svg class="copy-icon success" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="20,6 9,17 4,12"></polyline>
          </svg>
          <span class="copy-text">Copied</span>
        `;
        copyBtn.classList.add('copied');

        // 2秒后恢复原状态
        setTimeout(() => {
          copyBtn.innerHTML = `
            <svg class="copy-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
            </svg>
            <span class="copy-text">Copy</span>
          `;
          copyBtn.classList.remove('copied');
        }, 2000);

      } catch (err) {
        console.error('复制失败:', err);
        // 降级方案：使用旧的复制方法
        const textArea = document.createElement('textarea');
        textArea.value = codeBlock.textContent || '';
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);

        copyBtn.innerHTML = `
          <svg class="copy-icon success" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="20,6 9,17 4,12"></polyline>
          </svg>
          <span class="copy-text">Copied</span>
        `;
        copyBtn.classList.add('copied');

        setTimeout(() => {
          copyBtn.innerHTML = `
            <svg class="copy-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
            </svg>
            <span class="copy-text">Copy</span>
          `;
          copyBtn.classList.remove('copied');
        }, 2000);
      }
    });

    copyContainer.appendChild(copyBtn);

    // 设置pre元素为相对定位
    pre.style.position = 'relative';
    pre.appendChild(copyContainer);
  });
}
</script>

<style scoped lang="scss">
.docs {
  width: 1200px;
  height: 100vh;
  padding-top: 80px;
  margin: auto;
  position: relative;
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
    margin-left: 280px;
    box-sizing: border-box;
    display: flex;
    flex-direction: row;
    position: relative;

    .content {
      flex: 1;
      width: 680px;

      .markdown-content {
        width: 680px;
        margin-top: 70px;
        height: calc(100% - 80px);
        overflow-x: hidden;
        position: fixed;
        top: 0;
        bottom: 0;
        -ms-overflow-style: none;
        overflow-y: scroll;
        scrollbar-width: thin;
        padding-right: 20px;
      }
    }

    .toc-aside {
      position: fixed;
      right: calc(50% - 600px + 20px);
      top: 80px;
      width: 220px;
      height: calc(100vh - 80px);
      overflow-y: auto;
      border-left: 1px solid #ebebeb;
      background: white;
      z-index: 10;

      // 隐藏滚动条但保持滚动功能
      scrollbar-width: none; /* Firefox */
      -ms-overflow-style: none; /* IE and Edge */

      &::-webkit-scrollbar {
        display: none; /* Chrome, Safari, Opera */
      }

      .toc-wrapper {
        padding: 10px 15px 20px 15px;

        .toc-title {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 15px;
          padding-bottom: 8px;
          border-bottom: 1px solid #ebebeb;
        }
      }
    }
  }
}

// 代码复制按钮样式
:deep(.code-copy-container) {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 20;
}

:deep(.code-copy-btn) {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(4px);

  &:hover {
    background: rgba(0, 0, 0, 0.9);
    border-color: var(--sk-color-home-primary, #e25a1b);
    color: var(--sk-color-home-primary, #e25a1b);
    transform: scale(1.05);
  }

  &.copied {
    background: rgba(0, 0, 0, 0.9);
    border-color: var(--sk-color-home-primary, #e25a1b);
    color: var(--sk-color-home-primary, #e25a1b);

    .copy-icon.success {
      color: var(--sk-color-home-primary, #e25a1b);
    }
  }

  .copy-icon {
    width: 14px;
    height: 14px;
    stroke-width: 2;
    transition: color 0.2s ease;
  }

  .copy-text {
    font-weight: 500;
    white-space: nowrap;
  }
}

// 确保代码块容器有足够的空间显示复制按钮
:deep(pre) {
  position: relative !important;

  // 为复制按钮预留右上角空间，但不影响代码内容
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 80px;
    height: 40px;
    pointer-events: none;
  }

  &:hover .code-copy-container {
    opacity: 1;
  }
}

:deep(.code-copy-container) {
  opacity: 0.8;
  transition: opacity 0.2s ease;
}

// 响应式设计
@media (max-width: 1400px) {
  .docs {
    width: 100%;
    max-width: 1200px;

    .doc-content {
      .toc-aside {
        right: 20px;
      }
    }
  }
}

@media (max-width: 1200px) {
  .docs {
    width: 100%;

    .doc-content {
      margin-left: 250px;

      .content {
        width: calc(100vw - 280px);

        .markdown-content {
          width: calc(100vw - 280px);
        }
      }

      .toc-aside {
        display: none;
      }
    }
  }
}

@media (max-width: 768px) {
  .docs {
    .left-side {
      width: 200px;
    }

    .doc-content {
      margin-left: 200px;

      .content {
        width: calc(100vw - 220px);

        .markdown-content {
          width: calc(100vw - 220px);
        }
      }
    }
  }
}
</style>