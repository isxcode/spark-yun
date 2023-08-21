package com.isxcode.star.api.api.pojos.req;

import com.isxcode.star.api.api.constants.ApiType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApiAddApiReq {

  @Schema(title = "API名称", example = "测试Post接口")
  @NotEmpty(message = "API名称不能为空")
  private String name;

  @Schema(title = "API请求方式", example = ApiType.POST)
  @NotEmpty(message = "API请求方式不能为空")
  private String apiType;

  @Schema(title = "API请求地址", example = "test")
  @NotEmpty(message = "API请求地址不能为空")
  private String path;

  @Schema(
      title = "请求头",
      example =
          "[\n"
              + "    {\n"
              + "        \"key\":\"token\",\n"
              + "        \"value\":\"123456\",\n"
              + "        \"type\":\"TOKEN\"\n"
              + "    }\n"
              + "]")
  private String reqHeader;

  @Schema(
      title = "请求体",
      example =
          "{\n"
              + "    \"age\":\"${age}\",\n"
              + "    \"page\":\"${page}\",\n"
              + "    \"pageSize\":\"${pageSize}\"\n"
              + "}")
  private String reqBody;

  @Schema(
      title = "执行sql",
      example =
          "select username, age from ispong_db "
              + "where age > ${age} "
              + "limit (${page}-1)*${pageSize},${pageSize}")
  private String apiSql;

  @Schema(
      title = "响应体",
      example =
          "{\n"
              + "    \"code\":\"200\",\n"
              + "    \"message\":\"返回成功\",\n"
              + "    \"data\":[\n"
              + "        ${\n"
              + "            \"username\":\"${username}\"\n"
              + "            \"age\":\"${age}\"\n"
              + "        }$\n"
              + "    ]\n"
              + "    \"count\":\"${COUNT}\"\n"
              + "}")
  private String resBody;

  @Schema(title = "数据源id", example = "sy_a05cfc7bda0b41c196ada563052f68fe")
  private String datasourceId;

  @Schema(title = "备注", example = "该API为测试自定义API，请勿删除")
  private String remark;
}
