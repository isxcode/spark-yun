package com.isxcode.star.api.work.pojos.req;

import com.isxcode.star.api.work.pojos.dto.SyncColumnInfo;
import com.isxcode.star.api.work.pojos.dto.SyncColumnMap;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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

	@Schema(title = "来源数据库表名", example = "part_table")
	@NotEmpty(message = "来源数据库表名不能为空")
	private String sourceTable;

	@Schema(title = "来源数据库表的分区键", example = "part_column")
	@NotEmpty(message = "分区键不能为空")
	private String partitionColumn = "test_col";

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

	@Schema(title = "写入模式", example = "OVERWRITE or INTO")
	@NotEmpty(message = "写入模式不能为空")
	private String overMode;

	@Schema(title = "来源表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\",\"sql\":\"\"}]")
	@NotNull(message = "来源表信息不能为空")
	private List<SyncColumnInfo> sourceTableColumn;

	@Schema(title = "去向表信息", example = "[{\"code\":\"installed_rank\",\"type\":\"String\"}]")
	@NotNull(message = "去向表信息不能为空")
	private List<SyncColumnInfo> targetTableColumn;

	@Schema(title = "字段映射关系", example = "[{\"source\": \"installed_rank\",\"target\": \"installed_rank\"}]")
	@NotNull(message = "字段映射关系不能为空")
	private List<SyncColumnMap> columnMap;
}