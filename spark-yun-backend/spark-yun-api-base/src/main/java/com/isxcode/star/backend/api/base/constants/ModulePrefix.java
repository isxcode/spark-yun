package com.isxcode.star.backend.api.base.constants;

/** 每个模块的访问前缀. */
public interface ModulePrefix {

  /** 用户模块 USER. */
  String USER = "/usr";

  /** 计算集群模块. */
  String CLUSTER = "/cae";

  /** 集群节点模块. */
  String CLUSTER_NODE = "/eno";

  /** 数据源模块. */
  String DATASOURCE = "/das";

  /** 作业流模块. */
  String WORKFLOW = "/wof";

  /** 作业流收藏模块. */
  String WORKFLOW_FAVOUR = "/wff";

  /** 作业流配置模块. */
  String WORKFLOW_CONFIG = "wfc";

  /** 作业模块. */
  String WORK = "/wok";

  /** 作业配置模块. */
  String WORK_CONFIG = "woc";

  /** 云代理模块. */
  String YUN_AGENT = "/yag";

  /** 租户模块. */
  String TENANT = "/tet";

  /** 租户用户模块. */
  String TENANT_USER = "/tur";

  /** 许可证模块. */
  String LICENSE = "/lic";

  /** VIP作业模块. */
  String VIP_WORK = "/vip/wok";

  /** VIP作业实例模块. */
  String VIP_WORK_INSTANCE = "/vip/woi";

  /** VIP作业流实例模块. */
  String VIP_WORKFLOW_INSTANCE = "/vip/wfi";

  /** VIP自定义API模块. */
  String VIP_API = "/vip/api";

  /** VIP数据地图模块 */
  String VIP_DATA_MAP = "/vip/dmp";

  /** VIP数据资产模块 */
  String VIP_DATA_ASSET = "/vip/dat";

  /** VIP自定义表单 */
  String VIP_FORM = "/vip/fom";
}
