package com.isxcode.star.api.work.pojos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据同步配置信息.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UdfInfo {
  private String type;

  private String funcName;

  private String className;

  private String resultType;

}