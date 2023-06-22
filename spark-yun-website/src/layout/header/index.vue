<!--
 * @Author: fanciNate
 * @Date: 2023-05-05 15:04:54
 * @LastEditTime: 2023-06-22 21:11:13
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/layout/header/index.vue
-->
<template>
  <div class="zqy-header">
    <div class="header-name" @click="clickToSPK">
      <!-- <img src="../../assets/icons/logo.jpg" alt="至轻云"> -->
      至轻云
    </div>
    <div
      v-if="headerConfig.tenantList && headerConfig.tenantList.length > 0 && tenantSelect"
      class="zqy-tenant"
    >
      <!-- <div class="zqy-tenant"> -->
      <el-select
        v-model="tenantSelect"
        @change="tenantChange"
        @visible-change="visibleChange"
      >
        <el-option
          v-for="tenant in headerConfig.tenantList"
          :key="tenant.id"
          :label="tenant.name"
          :value="tenant.id"
        />
      </el-select>
    </div>
    <div class="header-user">
      <span class="redirect-url" @click="clickRedirectUrl">帮助文档</span>
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          <!-- {{ headerConfig?.userInfo?.username }}<i class="el-icon-arrow-down el-icon--right"></i> -->
          <el-avatar :size="32">{{ headerConfig.userInfo && headerConfig.userInfo.username ? headerConfig.userInfo.username.slice(0, 1) : "" }}</el-avatar>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <!-- <el-dropdown-item>个人信息</el-dropdown-item> -->
            <el-dropdown-item command="logout">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { useState, useMutations } from '@/hooks/useStore'
import { nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import { ChangeTenantData, QueryTenantList } from '@/services/login.service'
import eventBus from '@/utils/eventBus'
// import { GetTenantList } from '@/services/tenant-list.service'

const state = useState([ 'userInfo', 'tenantId' ], 'authStoreModule')
const mutations = useMutations([ 'setUserInfo', 'setToken', 'setTenantId', 'setRole', 'setCurrentMenu' ], 'authStoreModule')
const router = useRouter()

const tenantSelect = ref('')
let headerConfig = reactive({
  tenantList: [],
  userInfo: state.userInfo.value
})

function handleCommand(command: string): void {
  if (command === 'logout') {
    clearStore()
    router.push({
      name: 'login'
    })
  }
}

function clearStore() {
  mutations.setUserInfo({
  })
  mutations.setToken('')
  mutations.setTenantId('')
  mutations.setRole('')
  mutations.setRole('')
  mutations.setCurrentMenu('')
}

function getTenantList(): void {
  QueryTenantList()
    .then((res: any) => {
      headerConfig.tenantList = res.data || []
      res.data.forEach((item: any) => {
        if (item.currentTenant) {
          tenantSelect.value = item.id
        }
      })
      if (res.data && res.data.length > 0 && res.data.every((item: any) => !item.currentTenant)) {
        tenantChange(res.data[0].id)
      }
    })
    .catch(() => {
      headerConfig.tenantList = []
    })
}

function tenantChange(e: string): void {
  ChangeTenantData({
    tenantId: e
  })
    .then(() => {
      console.log('切换成功')
      mutations.setTenantId(e)

      // 这里发送eventbus，刷新当前打开的页面
      eventBus.emit('tenantChange')
    })
    .catch(() => {
      tenantSelect.value = state.tenantId.value
      console.log('切换失败')
    })
}

function visibleChange(e: boolean): void {
  if (e) {
    getTenantList()
  }
}

function clickRedirectUrl(): void {
  window.open(import.meta.env.VITE_INFO_URL, '_blank')
}

function clickToSPK() {
  window.open(import.meta.env.VITE_SPARK_URL, '_blank')
}

onMounted(() => {
  nextTick(() => {
    getTenantList()
    eventBus.emit('tenantChange')
  })

  eventBus.on('tenantListUpdate', () => {
    getTenantList()
  })
})

onUnmounted(() => {
  eventBus.off('tenantListUpdate', () => {
    console.log('这里移除了bus')
  })
})
</script>

<style lang="scss">
.zqy-header {
  // min-width: 960px;
  height: 60px;
  box-shadow: $--app-box-shadow;
  background-color: $--app-light-color;
  display: flex;
  justify-content: space-between;
  z-index: 100;
  position: absolute;
  width: 100%;
  top: 0;
  left: 0;
  .header-name {
    font-size: $--app-logo-large-font-size;
    font-weight: $--app-base-font-weight;
    height: 100%;
    display: flex;
    align-items: center;
    width: 200px;
    justify-content: center;
    color: $--app-primary-color;
    cursor: pointer;
  }
  .zqy-tenant {
    position: absolute;
    left: 206px;
    height: 60px;
    display: flex;
    align-items: center;

    .el-input {
      --el-input-focus-border: #fff;
      --el-input-transparent-border: 0 0 0 0px;
      --el-input-border-color: #fff;
      --el-input-hover-border: 0px !important;
      --el-input-hover-border-color: #fff;
      --el-input-focus-border-color: #fff;
      --el-input-clear-hover-color: #fff;
      box-shadow: 0 0 0 0px !important;
      --el-input-border: 0px;
    }
    .el-select .el-input__wrapper.is-focus {
      box-shadow: 0 0 0 0px !important;
    }
    .el-select .el-input.is-focus .el-input__wrapper {
      box-shadow: 0 0 0 0px !important;
    }
    .el-select {
      --el-select-border-color-hover: #fff;
    }
  }
  .header-user {
    display: flex;
    height: 100%;
    align-items: center;
    // padding-right: 20px;
    .el-dropdown-link {
      cursor: default;
    }
    .el-dropdown {
      margin-right: 20px;
    }
    .el-avatar {
      background-color: $--app-primary-color;
      color: $--app-light-color;
      font-size: $--app-small-font-size;
    }

    .redirect-url {
      font-size: $--app-small-font-size;
      color: $--app-primary-color;
      margin-right: 12px;
      cursor: pointer;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}
.el-dropdown-menu {
  .el-dropdown-menu__item {
    font-size: $--app-small-font-size;
  }
}
</style>
