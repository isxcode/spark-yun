import { useAuthStore } from "@/store/useAuth"
import { computed, ref } from "vue"
import { useRouter } from "vue-router"
import { Switch, User } from '@element-plus/icons-vue'
import { useSwitchTenant, type TenantInfo } from '@/hooks/switch-tenant'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import { ChangeTenantData } from "@/services/login.service"
import { ElMessage } from "element-plus"

export type AvatarMenuCommand = 'logout' | 'personal-info'

export function useMenuAvatar() {
  let authStore = useAuthStore()
  let router = useRouter()
  let tenantVisible = ref(false)

  let username = computed(() => {
    return authStore.userInfo?.username?.slice(0, 1)
  })

  const { currentTenant, tenantList, initSwitchTenant, onTenantChange } = useSwitchTenant()

  const isAdmin = computed(() => authStore.userInfo?.role === 'ROLE_SYS_ADMIN')

  const activeTenant = computed(() => {
    if (!currentTenant.value.id) {
      let teantId = authStore.tenantId

      return tenantList.value.find(item => item.id === teantId)
    }

    return currentTenant.value
  })

  if (!isAdmin.value) {
    initSwitchTenant()
  }

  const handleCommand = function(command: AvatarMenuCommand) {
    if (command === 'logout') {
      authStore.$reset()
      router.push({ name: 'login' })
    } else if (command === 'personal-info') {
      router.push({ name: 'personalInfo' })
    }
  }

  const handleTenantChange = function(event: Event, tenant: TenantInfo) {
    event.stopPropagation()
    event.preventDefault()

    onTenantChange(tenant.id)

    tenantVisible.value = false

    ChangeTenantData({
      tenantId: tenant.id
    }).then(() => {
      
      ElMessage.success('租户切换成功')

      authStore.setTenantId(tenant.id)
    })
  }

  const renderTenantSwitch = function() {
    return (
      <div class="zyq-home__menu-tenant">
        <EllipsisTooltip class="zyq-home__menu-title" label={ activeTenant.value?.name} />
        <el-popover v-model:visible={tenantVisible.value} placement="right" trigger="click" show-arrow={false} popper-class="zyq-home__tenant-popover">
          {{
            default: () => (
              <div class="zyq-home__tenant-wrap">
                {
                  tenantList.value.map(tenant => (
                    <div class="zqy-home__menu-option" onClick={ (e) => handleTenantChange(e, tenant) }>
                      <EllipsisTooltip class="zyq-home__menu-text" label={ tenant.name } />
                    </div>
                  ))
                }
              </div>
            ),
            reference: () => ( <el-icon class="zyq-home__menu-icon"><Switch /></el-icon>)
          }}
        </el-popover>
      </div>
    )
  }

  return {
    renderMenuAvatar: () => (
      <el-popover placement="top-end" show-arrow={false} trigger="click" popper-class="zyq-home__menu-avatar">
        {{
          reference: () => (<el-avatar class="zyq-home__avatar" size={32}>{ username.value }</el-avatar>),
          default: () => (
            <>
              { !isAdmin.value ? renderTenantSwitch() : null }
              <div class="zqy-home__menu-option" onClick={ () => handleCommand('personal-info') }>
                <el-icon><User /></el-icon>
                个人信息
              </div>
              <div class="zqy-home__menu-option" onClick={ () => handleCommand('logout') }>
                <el-icon><switch-button /></el-icon>
                退出登录
              </div>
            </>
          )
        }}
      </el-popover>
    )
  }
}