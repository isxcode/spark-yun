import { QueryTenantList } from "@/services/login.service";
import { readonly, ref } from "vue";

export interface TenantInfo {
  id: string
  name: string
}

export function useSwitchTenant() {
  const currentTenant = ref<TenantInfo>({
    id: '',
    name: ''
  })

  const tenantList = ref<TenantInfo[]>([])

  const initSwitchTenant = function() {
    QueryTenantList().then(({ data }: { data: TenantInfo[] }) => {
      tenantList.value = data
    })
  }

  const onTenantChange = function(tenantId: TenantInfo['id']) {
    currentTenant.value = tenantList.value.find(item => item.id === tenantId) || {  
      id: '',
      name: ''
    }
  }

  return {
    currentTenant,
    tenantList: readonly(tenantList),

    initSwitchTenant,
    onTenantChange
  }
}