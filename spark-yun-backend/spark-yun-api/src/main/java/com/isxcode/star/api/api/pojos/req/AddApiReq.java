package com.isxcode.star.api.api.pojos.req;

import com.isxcode.star.api.api.pojos.dto.HeaderTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AddApiReq {

  @Schema(title = "API名称", example = "测试接口")
  @NotEmpty(message = "name不能为空")
  private String name;

  @Schema(title = "API请求方式", example = "POST / GET")
  @NotEmpty(message = "apiType不能为空")
  private String apiType;

  @Schema(title = "API请求地址", example = "/test")
  @NotEmpty(message = "path不能为空")
  private String path;

  @Schema(title = "数据源id", example = "sy_a05cfc7bda0b41c196ada563052f68fe")
  @NotEmpty(message = "datasourceId不能为空")
  private String datasourceId;

  @Schema(title = "验证方式", example = "ANONYMOUS 任何人 / SYSTEM 系统认证 / CUSTOM 自定义")
  @NotEmpty(message = "验证方式")
  private String tokenType;

  @Schema(title = "分页类型", example = "OPEN_PAGE 开启分页 / CLOSE_PAGE 关闭分页")
  @NotEmpty(message = "pageType不能为空")
  private String pageType;

  @Schema(title = "如果用户使用自定义认证方式，需要填写", example = "")
  private HeaderTokenDto headerToken;

  @Schema(title = "请求体", example = "{\n"
    + "    \"age\":\"${age}\",\n"
    + "    \"page\":\"${page}\",\n"
    + "    \"pageSize\":\"${pageSize}\"\n"
    + "}")
  private String reqBody;

  @Schema(title = "执行sql", example = "select username, age from ispong_db where age > ${age} limit $[${page}-1)*${pageSize}],${pageSize}")
  private String apiSql;

  @Schema(title = "响应体", example = "{\n" +
    "\"code\":\"200\",\n"
    + "    \"message\":\"返回成功\",\n"
    + "    \"${data}\":[\n"
    + "        {\n"
    + "            \"username\":\"${username}\"\n"
    + "            \"age\":\"${age}\"\n"
    + "        }\n"
    + "    ],\n"
    + "    \"count\":\"${COUNT}\"\n"
    + "}")
  private String resBody;

  @Schema(title = "备注", example = "备注123")
  private String remark;
}
