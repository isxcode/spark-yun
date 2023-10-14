import type { ElForm, FormRules } from "element-plus";
import { reactive, readonly, ref } from "vue";

export interface LoginModel {
  account: string,
  passwd: string
}

export function useLogin (callback: ((callback: LoginModel) => Promise<void>)) {
  const elFormRef = ref<InstanceType<typeof ElForm> | null>()
  const loginRule: FormRules = {
    account: [
      {
        required: true,
        message: '请输入用户名',
        trigger: [ 'blur', 'change' ]
      }
    ],
    passwd: [
      {
        required: true,
        message: '请输入密码',
        trigger: [ 'blur', 'change' ]
      }
    ]
  }
  let btnLoading = ref(false)
  let loginModel = reactive<LoginModel>({
    account: '',
    passwd: ''
  })

  const handleLogin = function() {
    elFormRef.value?.validate((isValid) => {
      if (isValid) {
        btnLoading.value = true
        callback({...loginModel}).finally(() => {
          btnLoading.value = false
        })
      }
    })
  }

  return {
    renderLoginForm: () => (
      <div class="zqy-login__form-wrap">
        <el-form 
          ref={elFormRef}
          class="zqy-login__form" 
          model={loginModel}
          rules={loginRule}
        >
          <el-form-item prop="account">
            <el-input 
              class="zqy-login__input" 
              v-model={loginModel.account} 
              placeholder="账号/邮箱/手机号"
            ></el-input>
          </el-form-item>
          <el-form-item prop="passwd">
            <el-input 
              class="zqy-login__input" 
              v-model={loginModel.passwd}
              type="password"
              show-password
              autocomplete="new-password"
              placeholder="密码"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-button 
          class="zqy-login__btn"
          type="primary"
          loading={btnLoading.value}
          onClick={handleLogin}
        >登录</el-button>
      </div>
    ),

    loginModel: readonly(loginModel)
  }
}