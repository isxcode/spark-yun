import { useAuthStore } from "@/store/useAuth"
import { computed, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { OfficeBuilding, Search, Setting } from '@element-plus/icons-vue'
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
  let route = useRoute()
  let menuVisible = ref(false)
  let tenantDialogVisible = ref(false)
  let selectedTenantId = ref('')
  let switchTenantLoading = ref(false)
  let tenantKeyword = ref('')

  let username = computed(() => {
    return authStore.userInfo?.username?.slice(0, 1)
  })

  const { tenantList, initSwitchTenant, onTenantChange } = useSwitchTenant()

  const isAdmin = computed(() => authStore.userInfo?.role === 'ROLE_SYS_ADMIN')
  const isBuiltInAdminAccount = computed(() => authStore.userInfo?.account === 'admin')
  const activeTenantName = computed(() => {
    const current = tenantList.value.find(item => item.id === authStore.tenantId)
    return current?.name || '切换租户'
  })
  const filteredTenantList = computed(() => {
    const keyword = tenantKeyword.value.trim().toLowerCase()
    if (!keyword) {
      return tenantList.value
    }
    return tenantList.value.filter(tenant => (tenant.name || '').toLowerCase().includes(keyword))
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

  const handleTenantSelect = function(tenant: TenantInfo) {
    selectedTenantId.value = tenant.id
  }

  const closeTenantDialog = function() {
    tenantDialogVisible.value = false
    selectedTenantId.value = authStore.tenantId
    tenantKeyword.value = ''
  }

  const confirmTenantSwitch = function() {
    if (!selectedTenantId.value || switchTenantLoading.value) {
      return
    }
    const targetTenantId = selectedTenantId.value
    if (authStore.tenantId === targetTenantId) {
      closeTenantDialog()
      return
    }

    switchTenantLoading.value = true
    onTenantChange(targetTenantId)

    ChangeTenantData({
      tenantId: targetTenantId
    }, targetTenantId).then(() => {
      getVipLicenseEnabled(true).finally(() => {
        const needBackToWorkflowList = ['workflow-page', 'work-item', 'workflow-detail'].includes(String(route.name || ''))
        const applyTenantContext = () => {
          authStore.setTenantId(targetTenantId)
          http.setHeader({
            tenant: targetTenantId
          })
        }

        ElMessage.success('租户切换成功')
        closeTenantDialog()
        if (needBackToWorkflowList) {
          router.replace({
            name: 'workflow'
          }).finally(() => {
            applyTenantContext()
          })
        } else {
          applyTenantContext()
        }
      })
    }).catch(() => {
      onTenantChange(authStore.tenantId)
    }).finally(() => {
      switchTenantLoading.value = false
    })
  }

  const openTenantDialog = function() {
    menuVisible.value = false
    selectedTenantId.value = authStore.tenantId
    tenantKeyword.value = ''
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
                {
                  !isBuiltInAdminAccount.value ? (
                    <div class="zqy-home__menu-option" onClick={ goPersonalInfo }>
                      <el-icon><Setting /></el-icon>
                      设置
                    </div>
                  ) : null
                }
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
          v-slots={{
            footer: () => (
              <div class="zyq-home__tenant-dialog-footer">
                <el-button onClick={ closeTenantDialog }>取消</el-button>
                <el-button
                  type="primary"
                  loading={switchTenantLoading.value}
                  disabled={!selectedTenantId.value}
                  onClick={ confirmTenantSwitch }
                >
                  确认切换
                </el-button>
              </div>
            )
          }}
        >
          <div class="zyq-home__tenant-dialog-header">
            <div class="zyq-home__tenant-dialog-title">切换租户</div>
            <el-input
              v-model={tenantKeyword.value}
              class="zyq-home__tenant-dialog-search"
              clearable
              placeholder="搜索租户"
              prefix-icon={Search}
            />
          </div>
          <div class="zyq-home__tenant-dialog-list">
            {
              filteredTenantList.value.map(tenant => (
                <div
                  class={[
                    'zyq-home__tenant-dialog-item',
                    authStore.tenantId === tenant.id ? 'is-current' : '',
                    selectedTenantId.value === tenant.id ? 'is-selected' : ''
                  ]}
                  onClick={ () => handleTenantSelect(tenant) }
                >
                  <div class="zyq-home__tenant-name">
                    <EllipsisTooltip class="zyq-home__menu-text" label={ tenant.name } />
                  </div>
                  { authStore.tenantId === tenant.id ? <span class="zyq-home__tenant-current">当前</span> : null }
                </div>
              ))
            }
            {
              !filteredTenantList.value.length ? (
                <div class="zyq-home__tenant-dialog-empty">暂无匹配租户</div>
              ) : null
            }
          </div>
        </el-dialog>
      </>
    )
  }
}
