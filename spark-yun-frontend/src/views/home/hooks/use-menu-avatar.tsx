import { useAuthStore } from "@/store/useAuth"
import { computed, ref } from "vue"
import { useRouter } from "vue-router"
import { OfficeBuilding, Setting } from '@element-plus/icons-vue'
import { useSwitchTenant, type TenantInfo } from '@/hooks/switch-tenant'
import EllipsisTooltip from '@/components/ellipsis-tooltip/ellipsis-tooltip.vue'
import { ChangeTenantData } from "@/services/login.service"
import { ElMessage } from "element-plus"
import { CheckLicenseStatus } from "@/services/license.service"
import { getVipLicenseEnabled, resetVipLicenseCache } from "@/utils/vip-license"
import { http } from "@/utils/http"

export type AvatarMenuCommand = 'logout'

export function useMenuAvatar() {
  let authStore = useAuthStore()
  let router = useRouter()
  let menuVisible = ref(false)
  let tenantDialogVisible = ref(false)

  let username = computed(() => {
    return authStore.userInfo?.username?.slice(0, 1)
  })

  const { tenantList, initSwitchTenant, onTenantChange } = useSwitchTenant()

  const isAdmin = computed(() => authStore.userInfo?.role === 'ROLE_SYS_ADMIN')
  const activeTenantName = computed(() => {
    const current = tenantList.value.find(item => item.id === authStore.tenantId)
    return current?.name || '切换租户'
  })

  if (!isAdmin.value) {
    initSwitchTenant()
  }

  const doLogout = function() {
    resetVipLicenseCache()
    setTimeout(() => {
      authStore.$reset()
    })
    ElMessage.success('退出成功')
    router.push({ name: 'login' })
  }

  const goPersonalInfo = function() {
    router.push({
      name: 'personalInfo'
    })
  }

  const handleCommand = function(command: AvatarMenuCommand) {
    if (command === 'logout') {
      CheckLicenseStatus()
        .catch(() => {
          // 退出时仅做许可证状态刷新，失败不影响退出流程
        })
        .finally(() => {
          doLogout()
        })
    }
  }

  const handleTenantChange = function(tenant: TenantInfo) {
    if (authStore.tenantId === tenant.id) {
      tenantDialogVisible.value = false
      return
    }

    onTenantChange(tenant.id)
    tenantDialogVisible.value = false

    ChangeTenantData({
      tenantId: tenant.id
    }, tenant.id).then(() => {
      getVipLicenseEnabled(true).finally(() => {
        ElMessage.success('租户切换成功')

        authStore.setTenantId(tenant.id)
        http.setHeader({
          tenant: tenant.id
        })
      })
    }).catch(() => {
      onTenantChange(authStore.tenantId)
    })
  }

  const openTenantDialog = function() {
    menuVisible.value = false
    tenantDialogVisible.value = true
  }

  return {
    renderMenuAvatar: () => (
      <>
        <el-popover v-model:visible={menuVisible.value} placement="top-end" show-arrow={false} trigger="click" popper-class="zyq-home__menu-avatar">
          {{
            reference: () => (<el-avatar class="zyq-home__avatar" size={32}>{ username.value }</el-avatar>),
            default: () => (
              <>
                {
                  !isAdmin.value ? (
                    <div class="zqy-home__menu-option" onClick={ openTenantDialog }>
                      <el-icon><OfficeBuilding /></el-icon>
                      <EllipsisTooltip class="zyq-home__menu-text" label={ activeTenantName.value } />
                    </div>
                  ) : null
                }
                <div class="zqy-home__menu-option" onClick={ goPersonalInfo }>
                  <el-icon><Setting /></el-icon>
                  设置
                </div>
                <div class="zqy-home__menu-option" onClick={ () => handleCommand('logout') }>
                  <el-icon><switch-button /></el-icon>
                  退出登录
                </div>
              </>
            )
          }}
        </el-popover>
        <el-dialog
          v-model={tenantDialogVisible.value}
          width="420px"
          align-center
          append-to-body
          show-close={false}
          close-on-click-modal={false}
          close-on-press-escape={false}
          class="zyq-home__tenant-dialog"
        >
          <div class="zyq-home__tenant-dialog-list">
            {
              tenantList.value.map(tenant => (
                <div
                  class={['zyq-home__tenant-dialog-item', authStore.tenantId === tenant.id ? 'is-active' : '' ]}
                  onClick={ () => handleTenantChange(tenant) }
                >
                  <div class="zyq-home__tenant-name">
                    <EllipsisTooltip class="zyq-home__menu-text" label={ tenant.name } />
                  </div>
                  { authStore.tenantId === tenant.id ? <span class="zyq-home__tenant-current">当前</span> : null }
                </div>
              ))
            }
          </div>
        </el-dialog>
      </>
    )
  }
}
