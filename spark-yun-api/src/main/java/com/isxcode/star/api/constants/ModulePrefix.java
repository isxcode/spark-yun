package com.isxcode.star.api.constants;

/** 各个模块的请求路径前缀. */
public interface ModulePrefix {

  /** 用户模块 USER. */
  String USER = "/usr";

  /** 计算引擎模块. */
  String CALCULATE_ENGINE = "/cae";

  /** 引擎节点模块. */
  String ENGINE_NODE = "/eno";

  /** 数据源模块. */
  String DATASOURCE = "/das";

  /** 作业流模块. */
  String WORKFLOW = "/wof";

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

  /** 企业作业模块. */
  String VIP_WORK = "/vip/wok";

  /** 企业API模块. */
  String VIP_API = "/vip/api";

  /** 数据地图模块. DATA_MAP */
  String VIP_DATA_MAP = "/vip/dmp";

  /** 数据资产模块. DATA_ASSET */
  String VIP_DATA_ASSET = "/vip/dat";

  /** 自定义表单. VIP_FORM */
  String VIP_FORM = "/vip/fom";
}
