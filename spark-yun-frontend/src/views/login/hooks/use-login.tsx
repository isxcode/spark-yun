import type { ElForm, FormRules } from "element-plus";
import { reactive, readonly, ref } from "vue";

import logo from '@/assets/imgs/logo1.svg';
import { OauthUrlList } from "@/services/login.service";

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
  let oauthUrlList = ref([])
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

  const handleShow = function() {
    OauthUrlList().then((res: any) => {
      oauthUrlList.value = res.data
    }).catch(() => {
      oauthUrlList.value = []
    })
  }
  const handleRedirect = function(e: any) {
    location.href = e.invokeUrl
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
          type="primary"
          loading={btnLoading.value}
          onClick={handleLogin}
        >确认登录</el-button>
        <div class="oauth-login">
          <el-popover
            trigger="click"
            placement="bottom"
            width={300}
            onShow={handleShow}
            v-slots={{
                reference: () => <span class="oauth-login-text">免密登录</span>,
                default: () =>
                <div class="oauth-redirect-url">
                  {
                    !oauthUrlList.value.length ?
                    <div style="margin: auto;">暂无数据</div> :
                    oauthUrlList.value.map(item => <el-button type="primary" onClick={ () => handleRedirect(item)}>{item.name}</el-button>)
                  }
                </div>
            }}>
          </el-popover>
        </div>
      </div>
    ),

    loginModel: readonly(loginModel)
  }
}