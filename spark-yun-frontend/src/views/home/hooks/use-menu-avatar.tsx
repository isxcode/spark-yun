import { useAuthStore } from "@/store/useAuth"
import { computed } from "vue"
import { useRouter } from "vue-router"

export type AvatarMenuCommand = 'logout' | 'personal-info'

export function useMenuAvatar() {
  let authStore = useAuthStore()
  let router = useRouter()
  let username = computed(() => {
    return authStore.userInfo?.username?.slice(0, 1)
  })

  const handleCommand = function(command: AvatarMenuCommand) {
    if (command === 'logout') {
      authStore.$reset()
      router.push({ name: 'login' })
    } else if (command === 'personal-info') {
      router.push({ name: 'personalInfo' })
    }
  }

  return {
    renderMenuAvatar: () => (
      <el-dropdown class="zyq-home__menu-avatar" placement="top-end" onCommand={handleCommand}>
        {{
          default: () => (<el-avatar class="zyq-home__avatar"size={32}>{ username.value }</el-avatar>),
          dropdown: () => (
            <el-dropdown-menu>
              <el-dropdown-item command="personal-info">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          )
        }}
      </el-dropdown>
    )
  }
}