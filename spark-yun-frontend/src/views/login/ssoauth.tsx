import { defineComponent, nextTick } from "vue";
import { OauthLogin } from "@/services/login.service";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "@/store/useAuth";

export default defineComponent({
    setup() {
        const router = useRouter()
        const authStore = useAuthStore()
        const handleLogin = () => {
            const urlParams = new URLSearchParams(window.location.search);
            // 获取单个参数
            const code = urlParams.get('code'); // "John"
            const clientId = urlParams.get('clientId');
            OauthLogin({code: code, clientId: clientId}).then((res: any) => {
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
            }).catch((error: any) => {
                console.error('请求失败，查看原因', error)
                ElMessage.error('登录失败，参数缺失')
            })
        }
        handleLogin()
        return () => (<div></div>)
    }
})