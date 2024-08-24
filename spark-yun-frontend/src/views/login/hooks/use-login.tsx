import type { ElForm, FormRules } from "element-plus";
import { reactive, readonly, ref } from "vue";

import logo from '@/assets/imgs/logo1.svg';

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
        message: '请输入账号/邮箱/手机号',
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
  const handleKeyup = function(e: any) {
    if (e.type === 'keyup' && e.key === 'Enter') {
      elFormRef.value?.validate((isValid) => {
        if (isValid) {
          btnLoading.value = true
          callback({...loginModel}).finally(() => {
            btnLoading.value = false
          })
        }
      })
    }
  }

  return {
    renderLoginForm: () => (
      <div class="zqy-login__form-wrap">
        <img src={logo}/>
        <div class="zqy-login__main__title">
          用户登录
        </div>
        <el-form
          ref={elFormRef}
          class="zqy-login__form"
          model={loginModel}
          rules={loginRule}
          onKeyup={handleKeyup}
          label-position="top"
        >
          <el-form-item prop="account">
            <el-input
              prefix-icon="User"
              class="zqy-login__input"
              v-model={loginModel.account}
              placeholder="请输入账号/邮箱/手机号"
            ></el-input>
          </el-form-item>
          <el-form-item prop="passwd">
            <el-input
              prefix-icon="Lock"
              class="zqy-login__input"
              v-model={loginModel.passwd}
              type="password"
              show-password
              autocomplete="new-password"
              placeholder="请输入密码"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-button
          class="zqy-login__btn"
          type="text"
          loading={btnLoading.value}
          onClick={handleLogin}
        >确认登录</el-button>
      </div>
    ),

    loginModel: readonly(loginModel)
  }
}