<template>
  <div class="nav-tree">
    <div class="menu-item-block" v-for="link in treeData" :key="link._path">
      <!-- 如果当前link，children长度不为0,渲染当前link为一个文件夹 menu-folder -->
      <div
        v-if="link.children"
        class="menu-folder"
        @click.stop="handleFolderClick(link)"
      >
        <div class="menu-folder-title">
          <span class="menu-folder-title-icon"></span>
          <span class="menu-folder-title-text">{{ link.title }}</span>
        </div>
        <div class="menu-folder-content">
          <NavTree
            v-if="link.children"
            :treeData="link.children"
            @item-click="handleMenuItemClick"
          ></NavTree>
        </div>
      </div>
      <!-- 如果当前link，children长度为0,渲染当前link为一个menu-item -->
      <div
        v-if="!link.children"
        class="menu-item"
        @click.stop="handleMenuItemClick(link)"
      >
        <span class="menu-item-text">{{ link.title }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
interface NavItem {
  title: string;
  _path: string;
  _id?: string;
  _draft?: boolean;
  isCollapsed?: boolean;
  children?: NavItem[];
  [key: string]: any;
}
const { treeData } = defineProps({
  treeData: {
    type: Array as PropType<NavItem[]>,
    required: true,
  },
});
// 定义emit事件, 用于点击menu-item时触发，ts
const emit  = defineEmits(["item-click"]);

function  handleFolderClick(link: NavItem) {
  link.isCollapsed = !link.isCollapsed;
}

function handleMenuItemClick(link: NavItem) {
  console.log(link, "link");
  
  emit("item-click", link);
}
</script>

<style lang="scss" scope>
.nav-tree {
  .menu-folder {
    .menu-folder-title {
      display: flex;
      align-items: center;
      padding: 0 10px;
      height: 40px;
      cursor: pointer;
      &:hover {
        background-color: #f5f5f5;
      }
      .menu-folder-title-icon {
        width: 16px;
        height: 16px;
        background-color: #f5f5f5;
        border-radius: 50%;
        margin-right: 10px;
      }
      .menu-folder-title-text {
        font-size: 14px;
        font-weight: 500;
      }
    }
    .menu-folder-content {
      padding-left: 20px;
    }
  }
  .menu-item {
    padding: 0 10px;
    height: 40px;
    display: flex;
    align-items: center;
    cursor: pointer;
    &:hover {
      background-color: #f5f5f5;
    }
    .menu-item-text {
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>
