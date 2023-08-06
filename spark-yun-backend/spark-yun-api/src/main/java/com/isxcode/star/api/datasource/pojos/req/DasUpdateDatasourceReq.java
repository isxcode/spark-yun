package com.isxcode.star.api.datasource.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DasUpdateDatasourceReq {

  @Schema(title = "数据源唯一id", example = "sy_123456789")
  @NotEmpty(message = "数据源id不能为空")
  private String id;

  @Schema(title = "数据源名称", example = "本地mysql")
  @NotEmpty(message = "数据源名称不能为空")
  private String name;

  @Schema(title = "数据源jdbcUrl", example = "jdbc:mysql://localhost:3306")
  @NotEmpty(message = "地址不能为空")
  private String jdbcUrl;

  @Schema(title = "数据源用户名", example = "root")
  @NotEmpty(message = "用户名不能为空")
  private String username;

  @Schema(title = "数据源密码", example = "ispong123")
  private String passwd;

  @Schema(title = "备注", example = "该数据源为本地mysql数据源，请勿删除")
  private String remark;

  @Schema(title = "数据源类型", example = "mysql")
  @NotEmpty(message = "数据源类型不能为空")
  private String dbType;
}
