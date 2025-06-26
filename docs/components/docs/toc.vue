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
  color: #666;
  font-size: 14px;
  padding: 6px 8px;
  border-radius: 4px;
  margin-bottom: 4px;
  transition: all 0.2s;
  line-height: 1.4;
  display: block;
}

.toc-content-item-title:hover {
  color: #333;
  background-color: #f5f5f5;
}

.active {
  color: #e25a1b !important;
  background-color: #fff5f0;
  border-left: 3px solid #e25a1b;
}

.toc-content-item-children {
  /* margin-left: 20px; */
}

.md-h-1 {
  padding-left: 8px;
  font-weight: 600;
}

.md-h-2 {
  padding-left: 16px;
  font-weight: 500;
}

.md-h-3 {
  padding-left: 24px;
}

.md-h-4 {
  padding-left: 32px;
  font-size: 13px;
}

.md-h-5 {
  padding-left: 40px;
  font-size: 12px;
}

.md-h-6 {
  padding-left: 48px;
  font-size: 12px;
}
</style>
