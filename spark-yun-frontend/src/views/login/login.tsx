import { defineComponent, nextTick } from "vue";
import { useLogin, type LoginModel } from "./hooks/use-login";
import { LoginUserInfo } from "@/services/login.service";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "@/store/useAuth";

import logoURL from '@/assets/imgs/logo-view.png';
import logoIcon from '@/assets/imgs/logo-a.png'
import loginText from '@/assets/imgs/login-text.svg'
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
        <img class="zqy-logo-icon" src={logoIcon}/>
        <div class="zqy-login__body">
          <div class="zqy-login__playground">
            <img class="zqy-login__logo" src={logoURL} alt="logo" />
          </div>
          <div class="zqy-login__main">
            <div class="zqy-login__text">
              {/* <img src={loginText} /> */}
            </div>
            { renderLoginForm() }
          </div>
        </div>
      </div>
    )
  },
})