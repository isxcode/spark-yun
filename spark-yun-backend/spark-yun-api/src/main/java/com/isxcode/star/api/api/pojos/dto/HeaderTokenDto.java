package com.isxcode.star.api.api.pojos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HeaderTokenDto {

  @Schema(title = "自定义token的key", example = "key")
  private String tokenName;

  @Schema(title = "token令牌的值", example = "123456")
  private String value;
}
