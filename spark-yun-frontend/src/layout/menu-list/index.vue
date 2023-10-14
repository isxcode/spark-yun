<template>
  <div class="menu-list">
    <el-menu
      :default-active="defaultMenu"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      :unique-opened="true"
      @select="handleSelect"
    >
      <template
        v-for="menu in menuList"
        :key="menu.code"
      >
        <el-menu-item
          v-if="menu.authType.includes(configData.role)"
          :index="menu.code"
        >
          <el-icon><component :is="menu.icon" /></el-icon>
          <template #title>
            {{ menu.name }}
          </template>
        </el-menu-item>
      </template>
    </el-menu>
    <div
      class="collapse-btn"
      @click="clickToCollapse"
    >
      <template v-if="isCollapse">
        <el-icon><ArrowRight /></el-icon>
      </template>
      <template v-else>
        <el-icon><ArrowLeft /></el-icon>
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from '@/store/useAuth';
import { ref, defineProps, defineEmits, reactive } from 'vue'
// import { useState } from '@/hooks/useStore'

interface menu {
  icon: string;
  name: string;
  code: string;
  authType?: Array<string>;
}

const authStore = useAuthStore();
// const state = useState([ 'role' ], 'authStoreModule')
let isCollapse = ref(false)
const configData = reactive({
  role: authStore.role
})

defineProps<{
  menuList: {
    type: Array<menu>;
    default:() => [];
  };
  defaultMenu: string;
}>()
const emit = defineEmits([ 'select' ])

const handleSelect = (key: string, keyPath: string[]) => {
  emit('select', key)
}

function clickToCollapse() {
  isCollapse.value = !isCollapse.value
}
</script>

<style lang="scss">
// wraning global css
.el-menu-item {
  height: getCssVar('menu', 'item-height') !important;
  display: flex;
  align-items: center;
  &.is-active {
    background-color: getCssVar('color', 'primary', 'light-8') !important;
  }
}
.el-menu-item-group__title {
  display: none;
}
.menu-list {
  height: 100%;
  width: 100%;
  position: relative;
  border-right: solid 1px #e6e6e6;
  .el-menu {
    border-right: 0;
    .el-submenu {
      .el-menu-item-group__title {
        display: none;
      }
      .el-submenu__title {
        height: getCssVar('menu', 'item-height');
        display: flex;
        align-items: center;
      }
    }
    .el-menu-item {
      height: getCssVar('menu', 'item-height');
      display: flex;
      align-items: center;
      box-sizing: border-box;
      padding-right: 0;
      padding-left: 45px !important;
      .el-tooltip {
        display: flex !important;
        align-items: center;
      }
      &.is-active {
        background-color: getCssVar('color', 'primary', 'light-8');
      }
    }
  }
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
  }
  .collapse-btn {
    border: 1px solid getCssVar('border-color');
    width: 12px;
    height: 60px;
    border-radius: 0 12px 12px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    right: -14px;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
    opacity: 0.7;
    &:hover {
      background-color: getCssVar('color', 'white');
      opacity: 1;
    }
    .el-icon {
      color: getCssVar('color', 'info');
    }
  }
}
</style>
