package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SaveSyncWorkConfigReq {

	@Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
	@NotEmpty(message = "workId不能为空")
	private String workId;
  @Schema(title = "来源数据库类型", example = "mysql")
  @NotEmpty(message = "来源数据库类型不能为空")
  private String sourceDBType;
  @Schema(title = "来源数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
  @NotEmpty(message = "来源数据库id不能为空")
  private String sourceDBId;
  @Schema(title = "来源数据库表名", example = "part")
  @NotEmpty(message = "来源数据库表名不能为空")
  private String sourceTable;
  @Schema(title = "来源数据库查询条件", example = "WHERE id = 1")
  private String queryCondition;
  @Schema(title = "目标数据库类型", example = "mysql")
  @NotEmpty(message = "目标数据库类型不能为空")
  private String targetDBType;
  @Schema(title = "目标数据库唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
  @NotEmpty(message = "目标数据库id不能为空")
  private String targetDBId;
  @Schema(title = "目标数据库表名", example = "part")
  @NotEmpty(message = "目标数据库表名不能为空")
  private String targetTable;
  @Schema(title = "写入模式", example = "insert into")
  @NotEmpty(message = "写入模式不能为空")
  private String overMode;
  @Schema(title = "字段映射关系", example = "{\n" +
    "        \"installed_rank\": [\"installed_rank\", \"\"],\n" +
    "        \"version\": [\"version\",\"\"],\n" +
    "        \"description\": [\"description\",\"\"],\n" +
    "        \"type\": [null, \"type\",\"\"],\n" +
    "        \"script\": [\"script\",\"\"],\n" +
    "        \"checksum\": [\"checksum\",\"\"],\n" +
    "\"installed_by\": [\"installed_by\",\"\"],\n" +
    "\"installed_on\": [\"installed_on\",\"\"],\n" +
    "\"execution_time\": [\"execution_time\",\"\"],\n" +
    "  \"success\": [\"success\",\"\"]\n" +
    "    }")
  @NotEmpty(message = "字段映射关系")
  private String columMapping;
}
