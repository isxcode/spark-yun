<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="personal-info">
    <el-form ref="elFormRef" class="personal-info__form" :model="personalModel" label-position="top" :rule="personalRule">
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
      <el-form-item label="手机号">
        <el-input
          v-model="personalModel.phone"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="邮箱">
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
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { PersonalModel } from './personal-info'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import { useAuthStore } from "@/store/useAuth";
import { UpdateUserInfo } from "@/services/personal-info.service";
import { ElForm, ElMessage, FormRules } from "element-plus";

const authStore = useAuthStore()

const { userInfo } = authStore

const elFormRef = ref<InstanceType<typeof ElForm> | null>()

const personalRule: FormRules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: [ 'blur', 'change' ]
    }
  ]
}

const personalModel = reactive<PersonalModel>({
  username: userInfo.username || '',
  phone: userInfo.phone || '',
  email: userInfo.email || '',
  remark: userInfo.remark || ''
})

const breadCrumbList = [{
  name: '个人信息',
  code: 'personal-info'
}]

const handleSave = function() {

  elFormRef.value?.validate(valid => {
    if (valid) {

      authStore.setUserInfo(Object.assign(userInfo, personalModel))

      UpdateUserInfo(personalModel).then((res: any) => {
        ElMessage.success(res.msg)
      })
    }
  })
}

</script>

<style lang="scss" src="./personal-info.scss"></style>