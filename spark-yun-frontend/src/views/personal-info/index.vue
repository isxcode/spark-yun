<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="personal-info">
    <div class="personal-info__layout">
      <div class="personal-info__menu">
        <el-menu :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="basic-info">基础信息</el-menu-item>
          <el-menu-item index="change-password">修改密码</el-menu-item>
        </el-menu>
      </div>

      <div class="personal-info__content">
        <template v-if="activeMenu === 'basic-info'">
          <el-form ref="elFormRef" class="personal-info__form" :model="personalModel" label-position="top" :rules="personalRule">
            <el-form-item label="账号">
              <el-input
                v-model="personalModel.account"
                placeholder="--"
                disabled
              />
            </el-form-item>
            <el-form-item
              label="用户名"
              prop="username"
            >
              <el-input
                v-model="personalModel.username"
                maxlength="100"
                placeholder="请输入"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="personalModel.phone"
                maxlength="100"
                placeholder="请输入"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="personalModel.email"
                maxlength="100"
                placeholder="请输入"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                v-model="personalModel.remark"
                show-word-limit
                type="textarea"
                maxlength="200"
                :autosize="{ minRows: 4, maxRows: 4 }"
                placeholder="请输入"
              />
            </el-form-item>
          </el-form>

          <el-button type="primary" @click="handleSave">保存</el-button>
        </template>

        <template v-else>
          <el-form
            ref="passwordFormRef"
            class="personal-info__form"
            :model="passwordModel"
            label-position="top"
            :rules="passwordRule"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordModel.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordModel.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordModel.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
          </el-form>

          <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { PasswordModel, PersonalModel } from './personal-info'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useAuthStore } from "@/store/useAuth";
import { UpdateMyPassword, UpdateUserInfo } from "@/services/personal-info.service";
import { ElForm, ElMessage, FormRules } from "element-plus";

const authStore = useAuthStore()

const { userInfo } = authStore

const elFormRef = ref<InstanceType<typeof ElForm> | null>()
const passwordFormRef = ref<InstanceType<typeof ElForm> | null>()
const activeMenu = ref('basic-info')

const validatePhone = (_: any, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback()
    return
  }

  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号'))
    return
  }

  callback()
}

const personalRule: FormRules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: [ 'blur', 'change' ]
    }
  ],
  phone: [
    {
      validator: validatePhone,
      trigger: [ 'blur', 'change' ]
    }
  ],
  email: [
    {
      required: true,
      message: '请输入邮箱',
      trigger: [ 'blur', 'change' ]
    },
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: [ 'blur', 'change' ]
    }
  ]
}

const personalModel = reactive<PersonalModel>({
  username: userInfo.username || '',
  account: userInfo.account || '',
  phone: userInfo.phone || '',
  email: userInfo.email || '',
  remark: userInfo.remark || ''
})

const passwordModel = reactive<PasswordModel>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_: any, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
    return
  }

  if (value !== passwordModel.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }

  callback()
}

const passwordRule: FormRules = {
  oldPassword: [
    {
      required: true,
      message: '请输入原密码',
      trigger: [ 'blur', 'change' ]
    }
  ],
  newPassword: [
    {
      required: true,
      message: '请输入新密码',
      trigger: [ 'blur', 'change' ]
    }
  ],
  confirmPassword: [
    {
      validator: validateConfirmPassword,
      trigger: [ 'blur', 'change' ]
    }
  ]
}

const breadCrumbList = [{
  name: '设置',
  code: 'personal-info'
}]

const handleMenuSelect = function(key: string) {
  activeMenu.value = key
}

const handleSave = function() {

  elFormRef.value?.validate(valid => {
    if (valid) {

      authStore.setUserInfo(Object.assign(userInfo, personalModel))

      const updateParams = {
        username: personalModel.username,
        phone: personalModel.phone,
        email: personalModel.email,
        remark: personalModel.remark
      }

      UpdateUserInfo(updateParams).then((res: any) => {
        ElMessage.success(res.msg)
      })
    }
  })
}

const handleChangePassword = function() {
  passwordFormRef.value?.validate(valid => {
    if (valid) {
      UpdateMyPassword(passwordModel).then((res: any) => {
        ElMessage.success(res.msg)
        passwordFormRef.value?.resetFields()
      })
    }
  })
}

</script>

<style lang="scss" src="./personal-info.scss"></style>
