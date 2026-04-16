import type { Menu } from "@/views/home/menu.config";
import { CheckLicenseStatus } from "@/services/license.service";

const VIP_MENU_CODE_SET = new Set([
  // 免密登录
  "oauth-management",
  // 数据规划
  "data-planning",
  "data-layer",
  "layer-area",
  "field-format",
  "data-model",
  "model-field",
  // 全局变量
  "global-variables",
  // 实时计算
  "realtime-computing",
  "computing-detail",
  // 计算容器
  "spark-container",
  // 依赖合集
  "lib-package",
  // 元数据
  "metadata-page",
  "metadata-management",
  "acquisition-task",
  "acquisition-instance",
  // 基线告警
  "message-management",
  "message-notifications",
  "warning-config",
  "warning-schedule",
  // 数据服务
  "data-server",
  "report-components",
  "report-item",
  "report-views",
  "report-views-detail",
  "access-rule",
  "custom-api",
  "custom-form",
  "form-list",
  "form-query",
  "form-setting"
]);

let cachedVipEnabled: boolean | null = null;
let pendingCheckPromise: Promise<boolean> | null = null;

export function isVipMenuCode(code?: string): boolean {
  return !!code && VIP_MENU_CODE_SET.has(code);
}

export async function getVipLicenseEnabled(forceRefresh = false): Promise<boolean> {
  if (!forceRefresh && cachedVipEnabled !== null) {
    return cachedVipEnabled;
  }

  if (pendingCheckPromise) {
    return pendingCheckPromise;
  }

  pendingCheckPromise = CheckLicenseStatus()
    .then((res: any) => {
      const status = res?.data?.status;
      cachedVipEnabled = status === "ENABLE";
      return cachedVipEnabled;
    })
    .catch(() => {
      cachedVipEnabled = false;
      return false;
    })
    .finally(() => {
      pendingCheckPromise = null;
    });

  return pendingCheckPromise;
}

export function resetVipLicenseCache(): void {
  cachedVipEnabled = null;
  pendingCheckPromise = null;
}

export function filterVipMenus(menuList: Menu[], vipEnabled: boolean): Menu[] {
  const result: Menu[] = [];

  menuList.forEach((menu) => {
    const isVip = isVipMenuCode(menu.code);
    if (isVip && !vipEnabled) {
      return;
    }

    const hasChildren = !!menu.children?.length;
    const children = hasChildren ? filterVipMenus(menu.children || [], vipEnabled) : undefined;

    if (hasChildren && (!children || !children.length)) {
      return;
    }

    result.push({
      ...menu,
      children
    });
  });

  return result;
}
