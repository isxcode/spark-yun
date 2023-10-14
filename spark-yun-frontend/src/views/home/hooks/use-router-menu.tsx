import { computed, h, ref, resolveComponent, withModifiers, type CSSProperties } from "vue"
import { useAuthStore } from "@/store/useAuth"
import type { Menu } from "../menu.config"
import { useRoute, useRouter } from "vue-router"
import { useMenuAvatar } from "./use-menu-avatar"

export function useRouterMenu(menuListData: Menu[]) {
  const authStore = useAuthStore()
  const route = useRoute()
  const router = useRouter()
  const { renderMenuAvatar } = useMenuAvatar()

  const menuViewData = computed(() => menuListData.filter(menuItem => menuItem.authType?.includes(authStore.role || 'ROLE_TENANT_MEMBER')))

  const currentMenu = computed(() => menuViewData.value.find(menuData => menuData.code === route.name))

  if (!currentMenu.value) {
    router.replace({
      name: menuViewData.value[0].code
    })
  } else {
    router.replace({
      name: currentMenu.value?.code
    })
  }

  let isCollapse = ref(false)

  let handleSelect = (index: Menu["code"]) => {
    router.push({
      name: index
    })
  }

  return {
    menuViewData,
    currentMenu,
    isCollapse,

    renderHomeMenu: () => (
      <div class="zqy-home__menu-wrap">
        <el-menu
          class="zqy-home__menu"
          collapse={isCollapse.value}
          default-active={currentMenu.value?.code}
          onSelect={handleSelect}
        >
          {
            menuViewData.value.map(menuData => (
              <el-menu-item
                key={menuData.code}
                index={menuData.code}
              >
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
          <el-icon 
            class="zqy-home__icon zqy-home__ops" 
            size={18}
            onClick={withModifiers(() => { isCollapse.value = !isCollapse.value }, ['native'])}
          >{ isCollapse.value ? <expand /> : <fold />}</el-icon>
        </div>
      </div>
    )
  }
}