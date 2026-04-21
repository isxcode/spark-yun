import { computed, h, onMounted, ref, resolveComponent, watch } from "vue"
import { useAuthStore } from "@/store/useAuth"
import type { Menu } from "../menu.config"
import { useRoute, useRouter } from "vue-router"
import { useMenuAvatar } from "./use-menu-avatar"
import { filterVipMenus, getLicenseApiAvailable, getVipLicenseEnabled } from "@/utils/vip-license"

function getCurrentMenu(menuList: Menu[], routeMenu: string, targetMenu?: Menu) {
  let currentMenu: any = null
  if (targetMenu) {
    return targetMenu
  }
  menuList.forEach(menu => {
    if (menu.children && menu.children.length) {
      currentMenu = getCurrentMenu(menu.children, routeMenu, currentMenu)
    }
    if (menu.code === routeMenu || menu.childPage?.includes(routeMenu)) {
      currentMenu = menu
    }
  })
  return currentMenu
}

export function useRouterMenu(menuListData: Menu[]) {
  const authStore = useAuthStore()
  const route = useRoute()
  const router = useRouter()
  const { renderMenuAvatar } = useMenuAvatar()
  const vipEnabled = ref(false)
  const licenseApiAvailable = ref(true)
  const vipChecked = ref(false)

  const menuViewData = computed(() => {
    const roleMenu = menuListData.filter(menuItem =>
      menuItem.authType?.includes(authStore.role || "ROLE_TENANT_MEMBER")
    )
    return filterVipMenus(roleMenu, vipEnabled.value, licenseApiAvailable.value)
  })

  const currentMenu = computed(() => {
    let m = menuViewData.value.find(menuData => menuData.code === route.name)
    if (!m) {
      const current = getCurrentMenu(menuViewData.value, route.name)
      // const current = menuViewData.value.find(m => m.childPage?.includes(route.name))
      return current
    } else {
      return m
    }
  })

  const loadVipLicense = async (forceRefresh = false) => {
    vipChecked.value = false
    vipEnabled.value = await getVipLicenseEnabled(forceRefresh)
    licenseApiAvailable.value = getLicenseApiAvailable()
    vipChecked.value = true
  }

  onMounted(async () => {
    await loadVipLicense()
  })

  watch(
    () => authStore.tenantId,
    async (tenantId, oldTenantId) => {
      if (!tenantId || tenantId === oldTenantId) {
        return
      }
      await loadVipLicense()
    }
  )

  watch(
    () => [vipChecked.value, route.name, menuViewData.value.length],
    () => {
      if (!vipChecked.value) {
        return
      }
      if (route.name === 'personalInfo') {
        return
      }
      if (!currentMenu.value && menuViewData.value.length) {
        router.replace({
          name: menuViewData.value[0].code
        })
      }
    },
    {
      immediate: true
    }
  )

  let isCollapse = ref(true)

  let handleSelect = (index: Menu["code"]) => {
    router.push({
      name: index
    })
  }

  watch(() => isCollapse.value, (newVal) => {
    authStore.setCollapse(newVal)
  })

  return {
    menuViewData,
    currentMenu,
    isCollapse,

    renderHomeMenu: () => (
      <div 
        class="zqy-home__menu-wrap" 
        onMouseenter={() => { isCollapse.value = false }} 
        onMouseleave={() => { isCollapse.value = true}}
      >
        <el-menu
          class="zqy-home__menu"
          unique-opened={true}
          collapse={isCollapse.value}
          default-active={currentMenu.value?.code}
          onSelect={handleSelect}
        >
          {
            menuViewData.value.map(menuData => (
              menuData.children && menuData.children.length ? 
                <el-sub-menu
                  key={menuData.code}
                  index={menuData.code}
                  v-slots = {{
                    title: () => (
                      isCollapse.value ? <el-icon class="zqy-home__icon">{ h(resolveComponent(menuData.icon)) }</el-icon> :
                      <span>
                        <el-icon class="zqy-home__icon">{ h(resolveComponent(menuData.icon)) }</el-icon>
                        <span class="zqy-home__text">{ menuData.name }</span>
                      </span>
                    )
                  }}
                >
                  {
                    menuData.children?.map(menu => (
                      <el-menu-item key={menu.code} index={menu.code}>
                        {{
                          default: () => (<el-icon class="zqy-home__icon">{ h(resolveComponent(menu.icon)) }</el-icon>),
                          title: () => (<span class="zqy-home__text">{ menu.name }</span>)
                        }}
                      </el-menu-item>
                    ))
                  }
                </el-sub-menu> :
                <el-menu-item key={menuData.code} index={menuData.code}>
                  {{
                    default: () => (<el-icon class="zqy-home__icon">{ h(resolveComponent(menuData.icon)) }</el-icon>),
                    title: () => (<span class="zqy-home__text">{ menuData.name }</span>)
                  }}
                </el-menu-item>
            ))
          }
        </el-menu>
        <div class="zqy-home__menu-footer">
          { renderMenuAvatar() }
          {/* <el-icon
            class="zqy-home__icon zqy-home__ops"
            size={18}
            onClick={withModifiers(() => { isCollapse.value = !isCollapse.value }, ['native'])}
          >{ isCollapse.value ? <expand /> : <fold />}</el-icon> */}
        </div>
      </div>
    )
  }
}
