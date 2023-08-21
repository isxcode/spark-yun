<template>
  <div class="layout-container">
    <!-- <div class="layout-left">
            <menu-list
                :menuList="menuListData"
                @select="select"
                :defaultMenu="defaultMenuData"
            ></menu-list>
        </div> -->
    <div class="layout-right">
      <div class="layout-right-header">
        <span>面包屑标题</span>
        <slot />
      </div>
      <router-view />
    </div>
  </div>
</template>

<script lang="ts" setup></script>
