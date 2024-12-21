<template>
  <div class="docs-toc">
    <div
      class="toc-content-item"
      v-for="(node, index) in contentDirTree"
      :key="index"
    >
      <SkEllipsis
        class="toc-content-item-title"
        :class="{ active: node.isActive, [`md-h-${node.hLevel}`]: true }"
        @click.stop="handleClick(node)"
        truncated
      >
        {{ node.title }}
      </SkEllipsis>
      <div
        v-if="node.children && node.children.length > 0"
        class="toc-content-item-children"
      >
        <template
          v-for="(child, childIndex) in node.children"
          :key="childIndex"
        >
          <DocsToc
            :contentDirTree="[child]"
            :activeNodeIdList="activeNodeIdList"
            @nodeClicked="handleClick"
          />
        </template>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { DirNode } from "~/interface/docs.interface";

const { contentDirTree, activeNodeIdList } = defineProps({
  contentDirTree: {
    type: Array as PropType<DirNode[]>,
    required: true,
  },
  activeNodeIdList: {
    type: Array as PropType<String[]>,
    default: () => [],
  },
});

const emit = defineEmits(["nodeClicked"]);

const resetNodeActiveStatus = (node) => {
  node.isActive = false;
  if (node.children) {
    node.children.forEach((child) => {
      resetNodeActiveStatus(child);
    });
  }
};

const handleClick = (node) => {
  emit("nodeClicked", node);
};

const isActive = (id) => {
  // 寻找当前节点是否在激活节点列表中
  return activeNodeIdList.includes(id);
};
</script>

<style scoped>
.toc-content-item {
  width: 100%;
  cursor: pointer;
}

.toc-content-item-title {
  color: var(--sk-color-font-menu);
  font-size: 16px;
  padding: 2px 0 2px 2px;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.2s;
}

.toc-content-item-title:hover {
  color: var(--sk-color-font-menu-hover);
  border-right: 1px solid var(--sk-color-home-primary);
}

.active {
  color: var(--sk-color-home-primary) !important;
  border-right: 2px solid var(--sk-color-home-primary);
}

.toc-content-item-children {
  /* margin-left: 20px; */
}

.md-h-1 {
  padding-left: 12px;
}

.md-h-2 {
  padding-left: 24px;
}

.md-h-3 {
  padding-left: 36px;
}

.md-h-4 {
  padding-left: 48px;
}

.md-h-5 {
  padding-left: 60px;
}
</style>
