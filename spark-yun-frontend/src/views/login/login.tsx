import { defineComponent, nextTick } from "vue";
import { useLogin, type LoginModel } from "./hooks/use-login";
import { LoginUserInfo } from "@/services/login.service";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "@/store/useAuth";

import logoURL from '@/assets/imgs/logo.png';
import './login.scss'

export default defineComponent({
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const handleLogin = (loginModel: LoginModel): Promise<void> => {
      return LoginUserInfo(loginModel).then((res: any) => {

        authStore.setUserInfo(res.data)
        authStore.setToken(res.data.token)
        authStore.setTenantId(res.data?.tenantId)
        authStore.setRole(res.data?.role)

        ElMessage.success(res.msg)

        nextTick(() => {
          router.push({
            name: 'home'
          })
        })
      }).finally(() => {
        return Promise.resolve()
      })
    }

    const { renderLoginForm } = useLogin(handleLogin)

    return () => (
      <div class="zqy-login">
        <div class="zqy-login__header">
          <img class="zqy-login__logo" src={logoURL} alt="logo" />
          <span class="zqy-login__title">至轻云</span>
        </div>
        <div class="zqy-login__body">
          <div class="zqy-login__playground">
            <span class="zqy-login__slogan">打造超轻量级大数据平台</span>
          </div>
          <div class="zqy-login__main">
            { renderLoginForm() }
          </div>
        </div>
      </div>
    )
  },
})