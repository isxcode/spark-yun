<!--
 * @Author: fanciNate
 * @Date: 2023-04-10 13:33:20
 * @LastEditTime: 2023-06-16 21:56:33
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/src/views/login/login.vue
-->
<template>
  <div class="zqy-login" @keyup.enter="clickToLogin">
    <div class="zqy-login-container">
      <div class="normal-login-title">
        至轻云
      </div>
      <el-form
        ref="loginForm"
        :model="formData"
        :rules="rules"
        class="normal-login-form"
      >
        <el-form-item
          label=""
          prop="account"
        >
          <el-input
            v-model="formData.account"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item
          label=""
          prop="passwd"
        >
          <el-input
            v-model="formData.passwd"
            type="password"
            placeholder="请输入密码"
            autocomplete="off"
            :show-password="true"
          />
        </el-form-item>
        <el-form-item class="login-button-content">
          <el-button
            class="login-button"
            type="primary"
            :loading="loading"
            @click="clickToLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <el-form-item class="registered-content">
          <span class="registered">基于spark打造超轻量级大数据平台
            <!-- <a class="do-registered" @click="clickRegistered"
                            >免费注册</a -->
          </span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { LoginUserInfo } from '@/services/login.service'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { useMutations } from '@/hooks/useStore'
import { useRouter } from 'vue-router'

const mutations = useMutations([ 'setUserInfo', 'setToken', 'setTenantId', 'setRole' ], 'authStoreModule')
const router = useRouter()

const loginForm = ref<FormInstance>()
const loading = ref(false)
const formData = reactive({
  account: '',
  passwd: ''
})
const rules = reactive<FormRules>({
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
})

function clickToLogin() {
  loading.value = true
  LoginUserInfo(formData)
    .then((res: any) => {
      mutations.setUserInfo(res.data)
      mutations.setToken(res.data.token)
      mutations.setTenantId(res.data?.tenantId)
      mutations.setRole(res.data?.role)
      loading.value = false
      router.push({
        name: 'home'
      })
      ElMessage.success(res.msg)
    })
    .catch((err: any) => {
      loading.value = false
      console.error(err)
    })
}
</script>

<style lang="scss">
.zqy-login {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  .zqy-login-container {
    width: 360px;
    height: 370px;
    padding: 32px 32px;
    box-sizing: border-box;
    border-radius: $--app-border-radius;
    background-color: rgba($color: $--app-light-color, $alpha: 1);
    box-shadow: $--app-box-shadow;
    position: relative;

    .normal-login-title {
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 32px;
      margin-top: 30px;
      margin-bottom: 30px;
    }
    .login-button-content {
      margin-top: 24px;
      .login-button {
        width: 100%;
      }
    }
    .registered-content {
      text-align: right;
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      .registered {
        font-size: 12px;
        width: 100%;
        text-align: center;
        .do-registered {
          color: $--app-primary-color;
          &:hover {
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>
