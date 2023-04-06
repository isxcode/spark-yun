package com.isxcode.star.api.menus;

import lombok.Getter;

public enum DatasourceStatusMenus {
  UN_CHECK("UN_CHECK", "未检测"),
  ;

  @Getter private final String status;

  @Getter private final String statusMeaning;

  DatasourceStatusMenus(String status, String statusMeaning) {
    this.status = status;
    this.statusMeaning = statusMeaning;
  }
}
